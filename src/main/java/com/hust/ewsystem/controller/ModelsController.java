package com.hust.ewsystem.controller;


import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.CommonData;
import com.hust.ewsystem.service.CommonDataService;
import com.hust.ewsystem.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/model")
public class ModelsController {

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;
    @Autowired
    private ModelsService modelsService;
    @Autowired
    private CommonDataService commonDataService;

    @GetMapping("/train")
    public EwsResult<?> train(@RequestBody Map<String, Object> FileForm) {
        // TODO 真实测点和标准测点的映射
        List<String> standPoints = (List<String>) FileForm.get("tableNames");
        // 参数校验
        if (standPoints == null || standPoints.isEmpty()) {
            return EwsResult.error("数据库名称缺失");
        }
        // 查询数据并提取 datetime和value列
        Map<LocalDateTime, Map<String, Object>> alignedData = new TreeMap<>();
        for (String standPoint : standPoints) {
            List<CommonData> data = commonDataService.selectAllData(standPoint);
            for (CommonData record : data) {
                LocalDateTime datetime = record.getDatetime();
                Double value = record.getValue();
                alignedData.computeIfAbsent(datetime, k -> new HashMap<>()).put(standPoint, value);
            }
        }
        // 写入 CSV 文件
        try (FileWriter csvWriter = new FileWriter(String.format("%s/train.csv", pythonFilePath))) {
            // 写入表头
            csvWriter.append("datetime");
            for (String standPoint : standPoints) {
                csvWriter.append(",").append(standPoint);
            }
            csvWriter.append("\n");
            // 写入数据
            for (Map.Entry<LocalDateTime, Map<String, Object>> entry : alignedData.entrySet()) {
                boolean allHaveValue = true;
                StringBuilder line = new StringBuilder(entry.getKey().toString());
                for (String standPoint : standPoints) {
                    Integer value = (Integer) entry.getValue().get(standPoint);
                    if (value == null) {
                        allHaveValue = false;
                        break;
                    }
                    line.append(",").append(value);
                }
                if (allHaveValue) {
                    csvWriter.append(line.toString()).append("\n");
                }

            }
        } catch (IOException e) {
            return EwsResult.error("写入 CSV 文件失败: " + e.getMessage());
        }
        // 算法调用
//        String result = modelsService.train(FileForm);
        // TODO 结果处理等
        return EwsResult.OK("666");
    }
}
