package com.hust.ewsystem;


import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        Runnable task1 = () -> System.out.println("Task 1 executed at " + System.currentTimeMillis());
        Runnable task2 = () -> System.out.println("Task 2 executed at " + System.currentTimeMillis());
        Runnable task3 = () -> System.out.println("Task 3 executed at " + System.currentTimeMillis());

        // 每隔 2 秒执行一次 Task 1
        ScheduledFuture<?> task1Future = scheduler.scheduleAtFixedRate(task1, 0, 2, TimeUnit.SECONDS);
        // 每隔 3 秒执行一次 Task 2
        ScheduledFuture<?> task2Future = scheduler.scheduleAtFixedRate(task2, 0, 3, TimeUnit.SECONDS);

        // 每隔 5 秒执行一次 Task 3
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(task3, 0, 5, TimeUnit.SECONDS);

        // 15秒后取消特定任务
        scheduler.schedule(() -> {
            System.out.println("Cancelling Task 1 and Task 2...");
            task1Future.cancel(true);
            task2Future.cancel(true);
        }, 15, TimeUnit.SECONDS);
        System.out.println("Task 1 and Task 2 will be cancelled after 15 seconds.");
        //关闭任务调度器
        // 可选：在取消任务后关闭调度器
        scheduler.schedule(() -> {
            System.out.println("Shutting down scheduler...");
            scheduler.shutdown();
        }, 30, TimeUnit.SECONDS);
    }
}
