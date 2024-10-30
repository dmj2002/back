package com.hust.ewsystem;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class test {
    public static void add() throws InterruptedException{
        long sum = 0;
        for (long i = 0; i < 1_000_000_000L; i++) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("检测到中断，提前退出计算");
                throw new InterruptedException("计算任务被中断");
            }
            sum += i;  // 模拟计算任务
        }
    }
    public static void main(String[] args) {
        try {
            // 创建 ProcessBuilder 实例
            ProcessBuilder processBuilder = new ProcessBuilder();

            // 设置命令和参数
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add("script.py");
            processBuilder.command(command);

            // 设置工作目录
            processBuilder.directory(new File("/path/to/working/directory"));

            // 合并标准输入流和错误输入流
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 获取输入流
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            // 读取进程输出
            int len;
            char[] buffer = new char[2048];
            StringBuilder output = new StringBuilder();
            while ((len = inputStreamReader.read(buffer)) != -1) {
                output.append(buffer, 0, len);
            }

            // 等待进程结束
            process.waitFor();

            // 输出进程结果
            System.out.println("Process output: " + output.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        final Map<String, String> taskStatusMap = new ConcurrentHashMap<>();
//        final ExecutorService executorService = Executors.newCachedThreadPool();
//        String taskId = UUID.randomUUID().toString();
//        taskStatusMap.put(taskId, "训练中");
//        // 创建一个异步任务
////        Future<?> future = CompletableFuture.runAsync(() -> {
////            try {
////                // 模拟耗时操作
////                Thread.sleep(10000);
////                System.out.println("异步任务执行完成");
////                taskStatusMap.put(taskId, "训练完成");
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////                taskStatusMap.put(taskId, "训练失败" + e.getMessage());
////            }
////        },executorService);
//        Future<?> future = executorService.submit(() -> {
//            try {
//                // 模拟耗时操作
//                add();
//                System.out.println("异步任务执行完成");
//                taskStatusMap.put(taskId, "训练完成");
//            } catch (InterruptedException e) {
//                System.out.println("任务被中断");
//                taskStatusMap.put(taskId, "训练失败" + e.getMessage());
//            }
//        });
//        System.out.println(taskId);
//        System.out.println(taskStatusMap.get(taskId));
//        if (future != null) {
//            boolean isCancelled = future.cancel(true);
//            if (isCancelled) {
//                taskStatusMap.put(taskId, "训练已被终止");
//            } else taskStatusMap.put(taskId, "训练终止失败");
//        } else {
//            taskStatusMap.put(taskId, "未找到任务: " + taskId);
//        }
//        Integer i =100;
//        while(i-- >0){
//            System.out.println(taskStatusMap.get(taskId));
//        }
//    }
}
