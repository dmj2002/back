package com.hust.ewsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.common.exception.EwsException;
import com.hust.ewsystem.computmgt.entity.WkFileForm;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    @Override
    public String train(Map<String, Object> FileForm) {
        try {
            executeShellCmd(FileForm);
        } catch (Exception e) {
            // 异常根据实际需求处理
        }
        return "666";
    }

    public Object executeShellCmd(Map<String, Object> FileForm) throws Exception {
        String csvFilePath = (String) FileForm.get("trainDataPath");
        String saveModelPath = (String) FileForm.get("saveModelPath");

        // 把需要的参数放在集合中
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("/001/train.py");
        command.add(csvFilePath);
        command.add(saveModelPath);

//      LOGGER.info("command: {}", JSON.toJSONString(command, SerializerFeature.WriteMapNullValue));

        // 调用算法
        Object result = execCmd(pythonFilePath, command);

        // TODO 实时计算结果数据格式我不知道，结果你自己处理了返回

        return null;
    }

    public Object execCmd(String filePath, List<String> command) throws Exception {
        //启动进程
        ProcessBuilder processBuilder = new ProcessBuilder();
        //定义命令内容
        processBuilder.directory(new File(filePath));
//        LOGGER.debug("命令执行目录: {}", processBuilder.directory());
        processBuilder.command(command);
        //将标准输入流和错误输入流合并，通过标准输入流读取信息
        processBuilder.redirectErrorStream(true);
        StringBuilder outputString = null;
        try {
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
        } catch (Exception e) {
//            LOGGER.error("算法调用异常,原因：{}", e.getMessage(), e);
            // 此异常由调用方处理
            throw new EwsException(e);
        }
        return outputString;
    }
}