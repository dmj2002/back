package com.hust.ewsystem.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.entity.tasks;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.mapper.TaskMapper;
import com.hust.ewsystem.service.ModelsService;

import com.hust.ewsystem.service.WarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelsServiceImpl.class);

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    @Autowired
    private WarningService warningService;
    @Autowired
    private TaskMapper tasksMapper;
    // 任务状态
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    @Override
    public String train(String algorithmLabel, String modelLabel,Integer modelId) {
        String taskLabel = UUID.randomUUID().toString();
        tasks newtask = new tasks();
        newtask.setModelId(modelId)
                .setTaskType(0)
                .setTaskLabel(taskLabel)
                .setStartTime(LocalDateTime.now());
        tasksMapper.insert(newtask);
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
        taskMap.put(modelLabel + "_train", scheduledTask);
        return taskLabel;
    }
    @Override
    public void predict(Integer alertInterval, String modelLabel, String algorithmLabel,Integer modelId) {
        Runnable task = () ->{
            prePredict(modelId,modelLabel,algorithmLabel);
        };
        // 定期调度任务
        ScheduledFuture<?> scheduledTask =scheduler.scheduleWithFixedDelay(task, 0, alertInterval, TimeUnit.SECONDS);
        taskMap.put(modelLabel + "_predict", scheduledTask);
    }

//    // 提供查询任务状态的接口
//    @Override
//    public Map<String, Object> getTaskStatus(String taskLabel) {
//        Map<String, Object> statusMap = new HashMap<>();
//        statusMap.put("taskLabel", taskLabel);
//        ScheduledFuture<?> scheduledTask = taskMap.get(taskLabel);
//        if (scheduledTask == null) {
//            statusMap.put("status", "任务不存在");
//        } else if (scheduledTask.isCancelled()) {
//            statusMap.put("status", "任务已取消");
//        } else if (scheduledTask.isDone()) {
//            statusMap.put("status", "任务已完成");
//        } else {
//            statusMap.put("status", "任务进行中");
//        }
//        return statusMap;
//    }

    /**
     * 终止任务
     * @param modelLabel
     * @return
     */
    @Override
    public String killTask(String modelLabel) {
        // 终止ScheduledFuture任务
        ScheduledFuture<?> scheduledTask = taskMap.get(modelLabel + "_predict");
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            taskMap.remove(modelLabel + "_predict");
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
    public void prePredict(int modelId, String modelLabel, String algorithmLabel) {
        try {
            String taskLabel = UUID.randomUUID().toString();
            tasks newtask = new tasks();
            newtask.setModelId(modelId)
                    .setTaskType(1)
                    .setTaskLabel(taskLabel)
                    .setStartTime(LocalDateTime.now());
            tasksMapper.insert(newtask);
            Integer taskId= newtask.getTaskId();
            File taskDir = new File(pythonFilePath + "/task_logs/" + taskLabel);
            if (!taskDir.exists()) {
                if (!taskDir.mkdirs()) {
                    throw new FileException("创建任务目录失败");
                }
            }
            //TODO 读取train.csv的最后100行并写入predict.csv(后续需要在定时任务中写)
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
            executePredict(pythonFilePath, algorithmLabel, taskLabel, modelId, taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void executePredict(String filepath, String algorithmLabel, String taskLabel,Integer modelId,Integer taskId) {
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
            System.out.println("Started Python process for model: " +modelId+ " task: " + taskLabel);
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
                LOGGER.info("Finished reading and saving results for task: " + taskLabel);
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

            List<JSONObject> alertJsonList = new ArrayList<>();
            for (int i = 0; i < alertList.size(); i++) {
                alertJsonList.add(alertList.getJSONObject(i));
            }

            // 预警信息入库及合并
            processAlerts(alertJsonList,modelId,taskId,formatter);

//            for (int i = 0; i < alertList.size(); i++) {
//                JSONObject alert = alertList.getJSONObject(i);
//                String alertInfo = alert.getString("alarm_info");
//                LocalDateTime startTime = LocalDateTime.parse(alert.getString("start_time"), formatter);
//                LocalDateTime endTime = LocalDateTime.parse(alert.getString("end_time"), formatter);
//
//                // 保存到数据库
//                Warnings warning = new Warnings();
//                warning.setModelId(modelId);
//                warning.setWarningDescription(alertInfo);
//                warning.setStartTime(startTime);
//                warning.setEndTime(endTime);
//                warning.setTaskId(taskId);
//                warning.setWarningStatus(0);//异常状态：未处理
//                warning.setWarningLevel(0);//set为一级先
//                warningService.save(warning);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 预警信息入库及合并
     * @param alertList alertList
     * @param modelId modelId
     * @param taskId taskId
     * @param formatter formatter
     */
    private void processAlerts(List<JSONObject> alertList, int modelId, int taskId,DateTimeFormatter formatter) {
        Iterator<JSONObject> iterator = alertList.iterator();
        JSONObject prevAlert = null;
        while (iterator.hasNext()) {
            JSONObject alert = iterator.next();
            String alertInfo = alert.getString("alarm_info");
            if(alertInfo.contains("正常")){
                continue;
            }
            LocalDateTime startTime = LocalDateTime.parse(alert.getString("start_time"), formatter);
            LocalDateTime endTime = LocalDateTime.parse(alert.getString("end_time"), formatter);

            // 保存到数据库
            Warnings warning = new Warnings();
            warning.setModelId(modelId);
            warning.setWarningDescription(alertInfo);
            warning.setStartTime(startTime);
            warning.setEndTime(endTime);
            warning.setTaskId(taskId);
            warning.setWarningStatus(0);
            warning.setWarningLevel(0);

            if (prevAlert!= null) {
                LocalDateTime prevStartTime = LocalDateTime.parse(prevAlert.getString("start_time"), formatter);
                LocalDateTime prevEndTime = LocalDateTime.parse(prevAlert.getString("end_time"), formatter);

                // 判断当前警报的开始时间是否在上一个警报的结束时间之前预警信息相同
                // 如果满足条件，更新上一个警告对象的结束时间为当前警报的结束时间，然后使用 continue 跳过保存当前警告对象
                if (prevAlert.getString("alarm_info").equals(alert.getString("alarm_info"))
                        && prevAlert.getString("alarm_level").equals(alert.getString("alarm_level"))
                        && startTime.isBefore(prevEndTime)) {
                    // 持久化更新上一个警告对象的结束时间
                    try {
                        LambdaQueryWrapper<Warnings> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(Warnings::getTaskId,taskId).eq(Warnings::getModelId,modelId)
                                        .eq(Warnings::getWarningLevel,0).eq(Warnings::getWarningStatus,0)
                                        .eq(Warnings::getWarningDescription,alertInfo).eq(Warnings::getStartTime,prevStartTime);
                        Warnings one = warningService.getOne(queryWrapper);
                        one.setEndTime(endTime);
                        warningService.updateById(one);
                    } catch (Exception e) {
                        LOGGER.error("Failed to update prevWarning: ", e);
                    }
                    continue;
                }
            }

            try {
                warningService.save(warning);
            } catch (Exception e) {
                LOGGER.error("Failed to save warning: " + warning, e);
            }
            prevAlert = alert;
        }
    }
}
