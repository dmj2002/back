package com.hust.ewsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.*;
import com.hust.ewsystem.mapper.RealPointMapper;
import com.hust.ewsystem.mapper.StandPointMapper;
import com.hust.ewsystem.mapper.StandRealRelateMapper;
import com.hust.ewsystem.service.CommonDataService;
import com.hust.ewsystem.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/model")
public class ModelsController {

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    @Autowired
    private ModelsService modelsService;

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private StandRealRelateMapper standRealRelateMapper;

    @Autowired
    private StandPointMapper standPointMapper;

    @Autowired
    private RealPointMapper realPointMapper;

    @PostMapping("/add")
    public EwsResult<?> addmodel(@RequestBody Models model){
        List<Models> modelsList = new ArrayList<>();
        for (String turbineId : model.getTurbineId()) {
            Models newModel = new Models();
            newModel.setModelName(model.getModelName());
            newModel.setTurbineId(Arrays.asList(turbineId));
            // TODO 其他字段映射
            modelsList.add(newModel);
        }
        modelsService.saveBatch(modelsList);
        return EwsResult.OK(null);
    }

    @PostMapping("/train")
    public EwsResult<?> train(@RequestBody Map<String, Object> FileForm) {
        Map<String, String> standToRealPointMap = new HashMap<>();
        List<String> standPoints = (List<String>) FileForm.get("standPoints");
        // 参数校验
        if (standPoints == null || standPoints.isEmpty()) {
            return EwsResult.error("数据库名称缺失");
        }
        //标准测点标签 -> 标准测点ID
        Map<String, Integer> standPointMap = standPointMapper.selectList(
                new QueryWrapper<StandPoint>().in("point_label", standPoints)
        ).stream().collect(Collectors.toMap(StandPoint::getPointLabel, StandPoint::getPointId));
         //标准测点ID -> 真实测点ID
        Map<Integer, Integer> standToRealMap = standRealRelateMapper.selectList(
                new QueryWrapper<StandRealRelate>().in("stand_point_id", standPointMap.values())
        ).stream().collect(Collectors.toMap(StandRealRelate::getStandPointId, StandRealRelate::getRealPointId));
        //真实测点ID -> 真实测点标签
        Map<Integer,String> RealPointMap = realPointMapper.selectList(
                new QueryWrapper<RealPoint>().in("point_id", standToRealMap.values())
        ).stream().collect(Collectors.toMap(RealPoint::getPointId, RealPoint::getPointLabel));
        //标准测点标签 -> 真实测点标签
        for (String standPointLabel : standPoints) {
            Integer standPointId = standPointMap.get(standPointLabel);
            Integer realPointId = standToRealMap.get(standPointId);
            String realPointLabel = RealPointMap.get(realPointId);
            standToRealPointMap.put(standPointLabel, realPointLabel);
        }
        // 查询数据并提取 datetime和value列
        Map<LocalDateTime, Map<String, Object>> alignedData = new TreeMap<>();
        for (Map.Entry<String,String> entry : standToRealPointMap.entrySet()) {
            List<CommonData> data = commonDataService.selectAllData(entry.getValue().toLowerCase());
            for (CommonData record : data) {
                LocalDateTime datetime = record.getDatetime();
                Double value = record.getValue();
                alignedData.computeIfAbsent(datetime, k -> new HashMap<>()).put(entry.getKey(), value);
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
//                boolean allHaveValue = true;
                StringBuilder line = new StringBuilder(entry.getKey().toString());
                for (String standPoint : standPoints) {
                    Double value = (Double) entry.getValue().get(standPoint);
//                    if (value == null) {
//                        allHaveValue = false;
//                        break;
//                    }
                    line.append(",").append(value);
                }
                csvWriter.append(line.toString()).append("\n");
            }
        } catch (IOException e) {
            return EwsResult.error("写入 CSV 文件失败: " + e.getMessage());
        }
        // 算法调用
        String taskId = modelsService.train(FileForm);
        // TODO 结果处理等
        return EwsResult.OK(taskId);
    }
    // 查询任务状态
    @PostMapping("/queryTask")
    public EwsResult<?>  getTaskStatus(@RequestBody Map<String,Object> taskForm) {
        List<String> taskIdList = (List<String>) taskForm.get("taskIdList");
        List<Map<String, Object>> taskStatus = new ArrayList<>();
        // 检查任务ID列表是否为空
        if (taskIdList == null || taskIdList.isEmpty()) {
            return EwsResult.error("任务ID列表不能为空");
        }
        for (String taskId : taskIdList) {
            Map<String, Object> onetaskStatus = modelsService.getTaskStatus(taskId);
            taskStatus.add(onetaskStatus);
        }
        return EwsResult.OK(taskStatus);
    }
    @DeleteMapping("/kill/{taskId}")
    public EwsResult<?> deleteTask(@PathVariable String taskId) {
        String killTask = modelsService.killTask(taskId);
        return EwsResult.OK(killTask);
    }

}
