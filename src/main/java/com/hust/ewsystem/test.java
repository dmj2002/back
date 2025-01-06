package com.hust.ewsystem;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.ewsystem.entity.Reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) throws IOException {
        String resultFilePath =  "C:/result.json";

        // 强制使用 UTF-8 编码读取文件内容
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(resultFilePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        }
        String content = contentBuilder.toString();

        // 解析 JSON 内容
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray alertList = jsonObject.getJSONArray("alarm_list");
//        List<JSONObject> alertJsonList = new ArrayList<>();
//        for (int i = 0; i < alertList.size(); i++) {
//            alertJsonList.add(alertList.getJSONObject(i));
//        }

        System.out.println(alertList);
    }
}
