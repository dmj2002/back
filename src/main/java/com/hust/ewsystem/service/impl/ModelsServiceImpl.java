package com.hust.ewsystem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.service.ModelsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;
    // 任务状态
    private final Map<String, Map<String, Object>> taskStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Process> processMap = new ConcurrentHashMap<>();

    @Override
    public String train(Map<String, Object> FileForm) {
        String taskId = UUID.randomUUID().toString();
        // 初始化任务状态
        Map<String, Object> taskStatus = new HashMap<>();
        taskStatus.put("taskId", taskId);
        taskStatus.put("status", "训练中");
        taskStatus.put("progress", 0);
        taskStatusMap.put(taskId, taskStatus);
        executeShellCmd(FileForm);
        return taskId;
    }
    // 提供查询任务状态的接口
    @Override
    public Map<String, Object> getTaskStatus(String taskId) {
        Map<String, Object> taskStatus = taskStatusMap.get(taskId);
        if (taskStatus != null) {
            taskStatus.put("taskId", taskId);
            return taskStatus;
        } else {
            // 返回一个表示任务不存在的状态
            Map<String, Object> notFoundStatus = new HashMap<>();
            notFoundStatus.put("taskId", taskId);
            notFoundStatus.put("progress", -1);
            notFoundStatus.put("msg", "未找到任务");
            return notFoundStatus;
        }
    }

    @Override
    public String killTask(String taskId) {
        Process process = processMap.get(taskId);
        String result;
        if (process != null && process.isAlive()) {
            process.destroy();
            try {
                if (!process.waitFor(5, TimeUnit.SECONDS)) { // 等待最多5秒
                    process.destroyForcibly(); // 如果未终止，则强制终止
                }
                result = "任务已被终止";
            } catch (InterruptedException e) {
                result = "任务终止时发生错误: " + e.getMessage();
            }
        }
        else {
            result = "任务ID无效";
        }
        taskStatusMap.remove(taskId);
        processMap.remove(taskId);
        return result;
    }

    public Object executeShellCmd(Map<String, Object> FileForm) {
        try {
            // 把需要的参数放在集合中
            List<String> command = new ArrayList<>();
            command.add("python");
//            command.add(String.format("%s/train.py", modelLabel));
            command.add("setting.json");
            // 调用算法
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(pythonFilePath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 更新任务状态
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
