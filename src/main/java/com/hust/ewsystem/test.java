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
    public static void main(String[] args) {
        String pythonFilePath = "C:/data";
        Integer algorithmId = 1000;
        String algorithmDir = pythonFilePath + "/A" + String.format("%04d", algorithmId);;
        System.out.println(algorithmDir);
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
