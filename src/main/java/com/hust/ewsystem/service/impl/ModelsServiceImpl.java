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
        //启动一个新线程执行训练任务
        new Thread(()->{
            try {
                executeShellCmd(FileForm, taskId);
                taskStatus.put("status", "训练完成");
                taskStatus.put("progress", 100);
            } catch (Exception e) {
                taskStatus.put("status", "训练失败: " + e.getMessage());
                taskStatus.put("progress", -1);
            }
        }).start();

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
    //可以阻塞线程等待运行结果
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
        try {
            //启动进程
            Process start = processBuilder.start();
            processMap.put(taskId, start);
            //获取输入流
            InputStream inputStream = start.getInputStream();
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
            // TODO 实时计算结果数据格式我不知道，算法输出你自己解析了返回

            inputStream.close();
            inputStreamReader.close();
            //阻塞当前线程，直到进程退出为止
            start.waitFor();
            int exitValue = start.exitValue();
            if (exitValue == 0) {
                System.out.println("进程正常结束");
                Map<String, Object> taskStatus = taskStatusMap.get(taskId);
                taskStatus.put("data", outputString);
            } else {
                System.out.println("进程异常结束");
            }
        } catch (Exception e) {
            throw new Exception("算法调用异常,原因："+e.getMessage());
        }
        return null;

    }
}
