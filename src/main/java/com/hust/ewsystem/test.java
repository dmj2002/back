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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) throws IOException {
        // 创建一个示例的 alignedData
        Map<String, Map<String, Object>> alignedData = new HashMap<>();

        // 添加一些数据，包含和不包含符合移除条件的条目
        alignedData.put("2023-01-01 00:00:00", new HashMap<String, Object>() {{
            put("GridPower", 10.0);  // 正常数据，不会被移除
        }});
        alignedData.put("2023-01-01 01:00:00", new HashMap<String, Object>() {{
            put("GridPower", -5.0);  // GridPower 小于 0，会被移除
        }});
        alignedData.put("2023-01-01 02:00:00", new HashMap<String, Object>() {{
            put("GridPower", 0.0);   // GridPower 等于 0，会被移除
        }});
        alignedData.put("2023-01-01 03:00:00", new HashMap<String, Object>() {{
            put("GridPower", 15.0);  // 正常数据，不会被移除
        }});

        // 获取移除前的大小
        int sizeBeforeRemoval = alignedData.size();
        System.out.println("移除前的数据大小: " + sizeBeforeRemoval);

        // 执行移除操作
        alignedData.entrySet().removeIf(entry -> {
            Map<String, Object> labelMap = entry.getValue(); // 获取每个时间点的标签数据
            // 如果 "GridPower" 列的值小于或等于 0，移除该时间点的数据
            return labelMap.containsKey("GridPower") && (Double) labelMap.get("GridPower") <= 0;
        });

        // 获取移除后的大小
        int sizeAfterRemoval = alignedData.size();
        System.out.println("移除后的数据大小: " + sizeAfterRemoval);

        // 判断是否有数据被移除
        boolean isRemoved = sizeBeforeRemoval > sizeAfterRemoval;
        System.out.println("是否有数据被移除: " + isRemoved);

        // 打印移除后的数据
        System.out.println("移除后的数据内容:");
        for (Map.Entry<String, Map<String, Object>> entry : alignedData.entrySet()) {
            System.out.println("时间点: " + entry.getKey() + ", 数据: " + entry.getValue());
        }
    }
}
