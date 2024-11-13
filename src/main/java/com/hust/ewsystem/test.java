package com.hust.ewsystem;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.ewsystem.entity.Warnings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class test {

    public static void main(String[] args) {
        try {
            String resultFilePath =  "C:/result.json";
            String content = new String(Files.readAllBytes(Paths.get(resultFilePath)));
            System.out.println(content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            // Extract modelId
            Integer modelId = jsonObject.getIntValue("modelId");
            // Extract alertList
            JSONArray alertList = jsonObject.getJSONArray("alertList");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < alertList.size(); i++) {
                JSONObject alert = alertList.getJSONObject(i);
                String alertInfo = alert.getString("alertInfo");
                LocalDateTime startTime = LocalDateTime.parse(alert.getString("startTime"), formatter);
                LocalDateTime endTime = LocalDateTime.parse(alert.getString("endTime"), formatter);
                // Save to database
                Warnings warning = new Warnings();
                warning.setModelId(modelId);
                warning.setWarningDescription(alertInfo);
                warning.setStartTime(startTime);
                warning.setEndTime(endTime);
                System.out.println(warning);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
