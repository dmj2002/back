package com.hust.ewsystem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.service.ModelsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    private final Map<String, String> taskStatusMap = new ConcurrentHashMap<>();

    @Override
    public String train(Map<String, Object> FileForm) {
        String taskId = UUID.randomUUID().toString();
        taskStatusMap.put(taskId, "训练中");
        CompletableFuture.runAsync(() -> {
            try {
                executeShellCmd(FileForm);
                // 更新任务状态和结果
                taskStatusMap.put(taskId, "训练完成");
            } catch (Exception e) {
                taskStatusMap.put(taskId, "训练失败" + e.getMessage());
            }
        });
        return taskId;
    }
    // 提供查询任务状态的接口
    @Override
    public String getTaskStatus(String taskId) {
        return taskStatusMap.getOrDefault(taskId, "任务ID无效");
    }

    public Object executeShellCmd(Map<String, Object> FileForm){
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
            Object result = execCmd(pythonFilePath, command);
            // 更新任务状态
        } catch (Exception e) {
            // 更新任务状态
        }
        return null;
    }
    public Object execCmd(String filePath, List<String> command) throws Exception {
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
//            LOGGER.info("算法执行结果：{}", outputString);

        // TODO 实时计算结果数据格式我不知道，算法输出你自己解析了返回

        inputStream.close();
        inputStreamReader.close();
        //阻塞当前线程，直到进程退出为止
        start.waitFor();
        int exitValue = start.exitValue();
//            LOGGER.debug("exitValue===" + exitValue);
        if (exitValue == 0) {
//                LOGGER.debug("进程正常结束");
            System.out.println("进程正常结束");
        } else {
//                LOGGER.debug("进程异常结束");
            System.out.println("进程异常结束");
        }
        return outputString;
    }
}
