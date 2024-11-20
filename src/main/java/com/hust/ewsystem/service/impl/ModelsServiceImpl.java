package com.hust.ewsystem.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.service.ModelsService;

import com.hust.ewsystem.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    // 任务状态
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    @Override
    public String train(String algorithmLabel, String modelLabel) {
        String taskId = UUID.randomUUID().toString();
        File taskDir = new File(pythonFilePath + "/task_logs/" + taskId);
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
        settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskId + "/predict.csv");
        settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskId + "/result.json");
        settings.put("logPath", pythonFilePath + "/task_logs/" + taskId + "/" + taskId + ".log");
        // 写入 setting.json 文件
        try (FileWriter fileWriter = new FileWriter(settingFile)) {
            fileWriter.write(settings.toJSONString());
        } catch (IOException e) {
            throw new FileException("setting.json文件配置失败",e);
        }
        Runnable task = () -> executeTrain(pythonFilePath, algorithmLabel, taskId);
        // 调度任务一次性执行
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, 0, TimeUnit.SECONDS);
        taskMap.put(taskId, scheduledTask);
        return taskId;

    }
    @Override
    public String predict(Integer alertInterval, String modelLabel, String algorithmLabel,Integer modelId) {
        String taskId = UUID.randomUUID().toString();
        File taskDir = new File(pythonFilePath + "/task_logs/" + taskId);
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
        settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskId + "/predict.csv");
        settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskId + "/result.json");
        settings.put("logPath", pythonFilePath + "/task_logs/" + taskId + "/" + taskId + ".log");
        // 写入 setting.json 文件
        try (FileWriter fileWriter = new FileWriter(settingFile)) {
            fileWriter.write(settings.toJSONString());
        } catch (IOException e) {
            throw new FileException("setting.json文件配置失败",e);
        }
        Runnable task = () ->{
            try {
                executePredict(pythonFilePath, algorithmLabel, taskId,modelId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // 定期调度任务
        ScheduledFuture<?> scheduledTask =scheduler.scheduleWithFixedDelay(task, 0, alertInterval, TimeUnit.SECONDS);
        taskMap.put(taskId, scheduledTask);
        return taskId;
    }
    // 提供查询任务状态的接口
    @Override
    public Map<String, Object> getTaskStatus(String taskId) {
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("taskId", taskId);
        ScheduledFuture<?> scheduledTask = taskMap.get(taskId);
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

    @Override
    public String killTask(String taskId) {
        // 终止ScheduledFuture任务
        ScheduledFuture<?> scheduledTask = taskMap.get(taskId);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            taskMap.remove(taskId);
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
     * @param taskId 任务ID
     */
    public void executeTrain(String filepath, String algorithmLabel, String taskId) {
        Process process = null;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add(String.format("alg/%s/train.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskId));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for task: " + taskId);
            // 等待进程完成
            process.waitFor();
        } catch(InterruptedException e) {
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void executePredict(String filepath, String algorithmLabel, String taskId,Integer modelId) {
        //TODO 生成预测文件
        Process process = null;
        boolean interrupted = false;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add(String.format("alg/%s/predict.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskId));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for task: " + taskId);
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
                readAndSaveResults(filepath, taskId, modelId);
                System.out.println("Finished reading and saving results for task: " + taskId);
            }
        }
    }
    private void readAndSaveResults(String filepath, String taskId,Integer modelId) {
        try {
            String resultFilePath = filepath + "/task_logs/" + taskId + "/result.json";
            String content = new String(Files.readAllBytes(Paths.get(resultFilePath)));
            JSONObject jsonObject = JSONObject.parseObject(content);
            // Extract alertList
            JSONArray alertList = jsonObject.getJSONArray("alertList");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < alertList.size(); i++) {
                JSONObject alert = alertList.getJSONObject(i);
                String alertInfo = alert.getString("alertInfo");
                LocalDateTime startTime = LocalDateTime.parse(alert.getString("startTime"), formatter);
                LocalDateTime endTime = LocalDateTime.parse(alert.getString("endTime"), formatter);
                // Save to database
                Warnings warning = new Warnings();
                warning.setModelId(modelId);
                warning.setWarningDescription(alertInfo);
                warning.setStartTime(startTime);
                warning.setEndTime(endTime);
                warningService.save(warning);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
