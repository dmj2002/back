package com.hust.ewsystem;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class test {
    public static void main(String[] args) {
         final Map<String, String> taskStatusMap = new ConcurrentHashMap<>();
        String taskId = UUID.randomUUID().toString();
        taskStatusMap.put(taskId, "训练中");
        // 创建一个异步任务
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                // 模拟耗时操作
                Thread.sleep(2000);
                System.out.println("异步任务执行完成");
                taskStatusMap.put(taskId, "训练完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
                taskStatusMap.put(taskId, "训练失败" + e.getMessage());
            }
        });
        System.out.println(taskId);
        System.out.println(taskStatusMap.get(taskId));
        try {
            future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println(taskStatusMap.get(taskId));


    }
}
