package com.hust.ewsystem;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.entity.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) throws IOException {
        tasks newtask = new tasks();
        newtask.setModelId(1)
                .setTaskType(1)
                .setTaskLabel("123")
                .setStartTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        System.out.println(newtask.toString());
    }
}
