package com.hust.ewsystem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.service.ModelsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;
    // 任务状态
    private final Map<String, String> taskStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Process> processMap = new ConcurrentHashMap<>();

    @Override
    public String train(Map<String, Object> FileForm) {
        String taskId = UUID.randomUUID().toString();
        taskStatusMap.put(taskId, "训练中");
        try {
            executeShellCmd(FileForm, taskId);
            taskStatusMap.put(taskId, "训练完成");
        } catch (Exception e) {
            taskStatusMap.put(taskId, "训练失败: " + e.getMessage());
        }
        return taskId;
    }
    // 提供查询任务状态的接口
    @Override
    public String getTaskStatus(String taskId) {
        String result = taskStatusMap.getOrDefault(taskId, "任务ID无效");
        if(result.equals("训练完成") || result.contains("训练失败")) {
            taskStatusMap.remove(taskId);
        }
        return result;
    }

    @Override
    public String killTask(String taskId) {
        Process process = processMap.get(taskId);
        if (process != null && process.isAlive()) {
            process.destroy();
            try {
                if (!process.waitFor(5, TimeUnit.SECONDS)) { // 等待最多5秒
                    process.destroyForcibly(); // 如果未终止，则强制终止
                }
                taskStatusMap.put(taskId, "任务已被终止");
            } catch (InterruptedException e) {
                taskStatusMap.put(taskId, "任务终止时发生错误: " + e.getMessage());
            }
        }
        String result = taskStatusMap.getOrDefault(taskId, "任务ID无效");
        taskStatusMap.remove(taskId);
        return result;
    }

    public Object executeShellCmd(Map<String, Object> FileForm, String taskId) throws Exception {
        try {
            String csvFilePath = (String) FileForm.get("trainDataPath");
            String saveModelPath = (String) FileForm.get("saveModelPath");

            // 把需要的参数放在集合中
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add("/001/train.py");
//        command.add(String.format("%d/train.py", modelId));
            command.add(csvFilePath);
            command.add(saveModelPath);
//      LOGGER.info("command: {}", JSON.toJSONString(command, SerializerFeature.WriteMapNullValue));
            // 调用算法
            Object result = execCmd(pythonFilePath, command, taskId);
            // 更新任务状态
        } catch (Exception e) {
            // 更新任务状态
            throw new Exception();
        }
        return null;
    }
    public Object execCmd(String filePath, List<String> command, String taskId) throws Exception {
        //启动进程
        ProcessBuilder processBuilder = new ProcessBuilder();
        //定义命令内容
        processBuilder.directory(new File(filePath));
        //LOGGER.debug("命令执行目录: {}", processBuilder.directory());
        processBuilder.command(command);
        //将标准输入流和错误输入流合并，通过标准输入流读取信息
        processBuilder.redirectErrorStream(true);
        StringBuilder outputString = null;
        //启动进程
        Process start = processBuilder.start();
        processMap.put(taskId, start);
        return null;
    }
}
