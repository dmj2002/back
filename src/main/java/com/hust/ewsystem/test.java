package com.hust.ewsystem;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) {
        // 创建一个带有 3 个线程的调度线程池
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        // 定义三个不同的任务
        List<Integer> a = new ArrayList<>();
        a.add(2);
        a.add(3);
        a.add(5);
        Runnable task1 = () -> System.out.println("Task 1 executed at " + System.currentTimeMillis());
        Runnable task2 = () -> System.out.println("Task 2 executed at " + System.currentTimeMillis());
        Runnable task3 = () -> System.out.println("Task 3 executed at " + System.currentTimeMillis());

        // 每隔 2 秒执行一次 Task 1
        ScheduledFuture<?> task1Future = scheduler.scheduleAtFixedRate(task1, 0, a.get(0), TimeUnit.SECONDS);
        // 每隔 3 秒执行一次 Task 2
        ScheduledFuture<?> task2Future = scheduler.scheduleAtFixedRate(task2, 0, a.get(1), TimeUnit.SECONDS);

        // 每隔 5 秒执行一次 Task 3
         scheduler.scheduleAtFixedRate(task3, 0, a.get(2), TimeUnit.SECONDS);
         return ;
    }
}
