package com.hust.ewsystem.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.entity.tasks;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.mapper.TaskMapper;
import com.hust.ewsystem.service.ModelsService;

import com.hust.ewsystem.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    @Autowired
    private WarningService warningService;
    @Autowired
    private TaskMapper tasksMapper;
    // 任务状态
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    private final Map<String, String> modelMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    @PostConstruct
    public void initModelMap() {
        List<tasks> tasks = tasksMapper.selectList(null);
        if (tasks.isEmpty()) {
            // 记录日志：没有获取到任何任务数据
            System.out.println("No tasks found in the database.");
        }
        for (tasks task : tasks) {
            if(task.getTaskType()==0) {
                modelMap.put(task.getModelId() + "_train", task.getTaskLabel());
            }
            else if(task.getTaskType()==1){
                modelMap.put(task.getModelId() + "_predict", task.getTaskLabel());
            }
        }
    }
    @Override
    public String train(String algorithmLabel, String modelLabel,Integer modelId) {
        String taskLabel;
        if(modelMap.getOrDefault(modelId + "_train",null)!=null){
            taskLabel = modelMap.get(modelId + "_train");
        }else{
            taskLabel = UUID.randomUUID().toString();
            tasks newtask = new tasks();
            newtask.setModelId(modelId)
                    .setTaskType(0)
                    .setTaskLabel(taskLabel)
                    .setStartTime(LocalDateTime.now());
            tasksMapper.insert(newtask);
            modelMap.put(modelId + "_train", taskLabel);
        }
        File taskDir = new File(pythonFilePath + "/task_logs/" + taskLabel);
        if (!taskDir.exists()) {
            if (!taskDir.mkdirs()) {
                throw new FileException("创建任务目录失败");
            }
        }
        //准备setting.json
        File settingFile = new File(taskDir, "setting.json");
        JSONObject settings = new JSONObject();
        settings.put("modelPath", pythonFilePath + "/" + modelLabel);
        settings.put("trainDataPath", pythonFilePath + "/" + modelLabel + "/train.csv");
        settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/predict.csv");
        settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/result.json");
        settings.put("logPath", pythonFilePath + "/task_logs/" + taskLabel + "/" + taskLabel + ".log");
        // 写入 setting.json 文件
        try (FileWriter fileWriter = new FileWriter(settingFile)) {
            fileWriter.write(settings.toJSONString());
        } catch (IOException e) {
            throw new FileException("setting.json文件配置失败",e);
        }
        Runnable task = () -> executeTrain(pythonFilePath, algorithmLabel, taskLabel);
        // 调度任务一次性执行
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, 0, TimeUnit.SECONDS);
        taskMap.put(taskLabel, scheduledTask);
        return taskLabel;
    }
    @Override
    public String predict(Integer alertInterval, String modelLabel, String algorithmLabel,Integer modelId) {
        String taskLabel;
        Integer taskId;
        if(modelMap.getOrDefault(modelId + "_predict",null)!=null){
            taskLabel = modelMap.get(modelId + "_predict");
            taskId = tasksMapper.selectOne(new QueryWrapper<tasks>().eq("task_label", taskLabel)).getTaskId();
        }else{
            taskLabel = UUID.randomUUID().toString();
            tasks newtask = new tasks();
            newtask.setModelId(modelId)
                    .setTaskType(1)
                    .setTaskLabel(taskLabel)
                    .setStartTime(LocalDateTime.now());
            tasksMapper.insert(newtask);
            taskId = newtask.getTaskId();
            modelMap.put(modelId + "_predict", taskLabel);
        }
        File taskDir = new File(pythonFilePath + "/task_logs/" + taskLabel);
        if (!taskDir.exists()) {
            if (!taskDir.mkdirs()) {
                throw new FileException("创建任务目录失败");
            }
        }
        //读取train.csv的最后100行并写入predict.csv(后续需要在定时任务中写)
        String trainFilePath = pythonFilePath + "/" + modelLabel + "/train.csv"; // 训练数据路径
        String predictFilePath = taskDir.getAbsolutePath() + "/predict.csv"; // 预测文件路径
        try (BufferedReader reader = new BufferedReader(new FileReader(trainFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(predictFilePath))) {
            // 读取表头（第一行）
            String header = reader.readLine(); // 读取表头
            // 写入表头到predict.csv
            writer.write(header);
            writer.newLine();
            // 使用 LinkedList 保持最新的 100 行
            int maxLines = 100;
            LinkedList<String> lastLines = new LinkedList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                // 如果超过100行，移除最旧的
                if (lastLines.size() == maxLines) {
                    lastLines.poll();
                }
                lastLines.add(line); // 添加当前行
            }
            // 将最后100行写入predict.csv文件
            for (String lastLine : lastLines) {
                writer.write(lastLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //准备setting.json
        File settingFile = new File(taskDir, "setting.json");
        JSONObject settings = new JSONObject();
        settings.put("modelPath", pythonFilePath + "/" + modelLabel);
        settings.put("trainDataPath", pythonFilePath + "/" + modelLabel + "/train.csv");
        settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/predict.csv");
        settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/result.json");
        settings.put("logPath", pythonFilePath + "/task_logs/" + taskLabel + "/" + taskLabel + ".log");
        // 写入 setting.json 文件
        try (FileWriter fileWriter = new FileWriter(settingFile)) {
            fileWriter.write(settings.toJSONString());
        } catch (IOException e) {
            throw new FileException("setting.json文件配置失败",e);
        }
        Runnable task = () ->{
            try {
                executePredict(pythonFilePath, algorithmLabel, taskLabel, modelId, taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // 定期调度任务
        ScheduledFuture<?> scheduledTask =scheduler.scheduleWithFixedDelay(task, 0, alertInterval, TimeUnit.SECONDS);
        taskMap.put(taskLabel, scheduledTask);
        return taskLabel;
    }

    // 提供查询任务状态的接口
    @Override
    public Map<String, Object> getTaskStatus(String taskLabel) {
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("taskLabel", taskLabel);
        ScheduledFuture<?> scheduledTask = taskMap.get(taskLabel);
        if (scheduledTask == null) {
            statusMap.put("status", "任务不存在");
        } else if (scheduledTask.isCancelled()) {
            statusMap.put("status", "任务已取消");
        } else if (scheduledTask.isDone()) {
            statusMap.put("status", "任务已完成");
        } else {
            statusMap.put("status", "任务进行中");
        }
        return statusMap;
    }

    /**
     * 终止任务
     * @param modelId
     * @return
     */
    @Override
    public String killTask(Integer modelId) {
        // 终止ScheduledFuture任务
        String taskLabel = modelMap.get(modelId + "_predict");
        ScheduledFuture<?> scheduledTask = taskMap.get(taskLabel);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
//            taskMap.remove(taskLabel);
//            modelMap.remove(modelId);
        }
        // 检查任务和线程是否都已终止
        if (scheduledTask == null) {
            return "任务不存在";
        } else {
            return "任务已终止";
        }
    }

    /**
     *
     * @param filepath 进程工作目录
     * @param algorithmLabel 算法标签
     * @param taskLabel 任务ID
     */
    public void executeTrain(String filepath, String algorithmLabel, String taskLabel) {
        Process process = null;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add(String.format("alg/%s/train.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskLabel));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for task: " + taskLabel);
            // 等待进程完成
            process.waitFor();
        } catch(InterruptedException e) {
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void executePredict(String filepath, String algorithmLabel, String taskLabel,Integer modelId,Integer taskId) {
        //TODO 生成预测文件
        Process process = null;
        boolean interrupted = false;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add(String.format("alg/%s/predict.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskLabel));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for task: " + taskLabel);
            StringBuilder outputString = null;
            //获取输入流
            InputStream inputStream = process.getInputStream();
            //转成字符输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            int len = -1;
            char[] c = new char[2048];
            outputString = new StringBuilder();
            //读取进程输入流中的内容
            while ((len = inputStreamReader.read(c)) != -1) {
                String s = new String(c, 0, len);
                outputString.append(s);
            }
            inputStream.close();
            inputStreamReader.close();
            // 等待进程完成
            process.waitFor();
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                System.out.println("进程正常结束");
            } else {
                System.out.println("进程异常结束");
            }
        } catch (InterruptedException e) {
            interrupted = true;  // 记录中断状态
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!interrupted) {
                readAndSaveResults(filepath, taskLabel, modelId, taskId);
                System.out.println("Finished reading and saving results for task: " + taskLabel);
            }
        }
    }
    private void readAndSaveResults(String filepath, String taskLabel,Integer modelId,Integer taskId) {
        try {
            String resultFilePath = filepath + "/task_logs/" + taskLabel + "/result.json";

            // 强制使用 UTF-8 编码读取文件内容
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(resultFilePath), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line);
                }
            }
            String content = contentBuilder.toString();

            // 解析 JSON 内容
            JSONObject jsonObject = JSONObject.parseObject(content);
            JSONArray alertList = jsonObject.getJSONArray("alarm_list");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < alertList.size(); i++) {
                JSONObject alert = alertList.getJSONObject(i);
                String alertInfo = alert.getString("alarm_info");
                LocalDateTime startTime = LocalDateTime.parse(alert.getString("start_time"), formatter);
                LocalDateTime endTime = LocalDateTime.parse(alert.getString("end_time"), formatter);

                // 保存到数据库
                Warnings warning = new Warnings();
                warning.setModelId(modelId);
                warning.setWarningDescription(alertInfo);
                warning.setStartTime(startTime);
                warning.setEndTime(endTime);
                warning.setTaskId(taskId);
                warning.setWarningStatus(0);//异常状态：未处理
                warning.setWarningLevel(0);//set为一级先
                warningService.save(warning);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
