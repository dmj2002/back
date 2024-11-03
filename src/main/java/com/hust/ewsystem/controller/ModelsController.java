package com.hust.ewsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.common.util.DateUtil;
import com.hust.ewsystem.entity.*;
import com.hust.ewsystem.mapper.RealPointMapper;
import com.hust.ewsystem.mapper.StandPointMapper;
import com.hust.ewsystem.mapper.StandRealRelateMapper;
import com.hust.ewsystem.service.CommonDataService;
import com.hust.ewsystem.service.ModelRealRelateService;
import com.hust.ewsystem.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ModelRealRelateService modelRealRelateService;

    @PostMapping("/add")
    @Transactional
    public EwsResult<?> addmodel(@RequestBody ModelForm modelform){
        //models表插入
        List<Models> modelsList = new ArrayList<>();
        for (Integer turbineId : modelform.getTurbineList()) {
            //先写传入的模型参数
            Models newModel = modelform.getModel();
            //后端自己生成的模型参数(没写modelLabel,更具主键生成的，徐娅萍mysql触发器写吧)
            newModel.setTurbineId(turbineId)
                    .setModelName(newModel.getModelName() + "_" + turbineId)
                    .setModelVersion("V1.0")
                    .setModelStatus(0)
                    .setAlertInterval(newModel.getAlertInterval() != null ? newModel.getAlertInterval() : 10);
            modelsList.add(newModel);
        }
        boolean saveBatch1 = modelsService.saveBatch(modelsList);
        //model_real_relate表插入
        List<String> standpointList = modelform.getPointList();
        Map<String, Integer> standToRealIdMap = standToRealId(standpointList);
        List<ModelRealRelate> modelRealRelateList = new ArrayList<>();
        for(String standPoint : standpointList){
            Integer realPointId = standToRealIdMap.get(standPoint);
            for (Models models : modelsList) {
                ModelRealRelate modelRealRelate = new ModelRealRelate();
                modelRealRelate.setModelId(models.getModelId())
                               .setRealPointId(realPointId);
                modelRealRelateList.add(modelRealRelate);
            }
        }
        boolean saveBatch2 = modelRealRelateService.saveBatch(modelRealRelateList);
        if(saveBatch1 && saveBatch2){
            List<Map<String,Object>> result = new ArrayList<>();
            //返回值
            for (Models models : modelsList) {
                Map<String,Object> map = new HashMap<>();
                map.put("modelId",models.getModelId());
                map.put("modelName",models.getModelName());
                map.put("modelVersion",models.getModelVersion());
                map.put("modelStatus",models.getModelStatus());
                map.put("turbinId",models.getTurbineId());
                result.add(map);
            }
            return EwsResult.OK(result);
        }
        else{
            //添加失败事务回滚
            throw new CrudException("模型关联批量保存失败");
        }
    }
    @PostMapping("/change")
    @Transactional
    public EwsResult<?> changemodel(@RequestBody List<ModelForm> model){
        //models表修改
        List<Models> modelsList = new ArrayList<>();
        for(ModelForm modelform : model){
            //传过来的参数直接修改
            Models newModel = modelform.getModel();
            String oldVersion = modelsService.getById(newModel.getModelId()).getModelVersion();
            String[] versionParts = oldVersion.split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0].substring(1)); //去掉v
            int minorVersion = Integer.parseInt(versionParts[1]);
            if (minorVersion < 9) {
                minorVersion++;
            } else {
                majorVersion += 1;
                minorVersion = 0;
            }
            String newVersion = "v" + majorVersion + "." + minorVersion;
            //像版本号这种
            newModel.setModelVersion(newVersion);
            modelsList.add(newModel);
        }
        boolean updateBatch = modelsService.updateBatchById(modelsList);
        if(!updateBatch){
            throw new CrudException("修改模型失败");

        }
        //model_real_relate表修改
        List<ModelRealRelate> modelRealRelateList = new ArrayList<>();
        for(ModelForm modelform : model){
            List<String> standpointList = modelform.getPointList();
            //表示当前模型没有修改测点
            if(standpointList == null || standpointList.isEmpty()){
                break;
            }
            Map<String, Integer> standToRealIdMap = standToRealId(standpointList);
            // 将 Map 的值收集到 Set中: A
            Set<Integer> realPointIdSet = new HashSet<>(standToRealIdMap.values());
            //当前模型的测点:B
            Set<Integer> exitrealPointIdSet = modelRealRelateService.list(
                    new QueryWrapper<ModelRealRelate>().eq("model_id", modelform.getModel().getModelId())
                    ).stream().map(ModelRealRelate::getRealPointId).collect(Collectors.toSet());
            //A - B = 需要插入的测点
            Set<Integer> insertSet = realPointIdSet.stream()
                    .filter(item -> !exitrealPointIdSet.contains(item))
                    .collect(Collectors.toSet());
            //B - A = 需要删除的测点
            Set<Integer> deleteSet = exitrealPointIdSet.stream()
                    .filter(item -> !realPointIdSet.contains(item))
                    .collect(Collectors.toSet());
            //插入(不需要主键，最后统一插入)
            if(!insertSet.isEmpty()){
                for(Integer realPointId : insertSet){
                    ModelRealRelate modelRealRelate = new ModelRealRelate();
                    modelRealRelate.setModelId(modelform.getModel().getModelId())
                                   .setRealPointId(realPointId);
                    modelRealRelateList.add(modelRealRelate);
                }
            }
            //删除
            if (!deleteSet.isEmpty()) {
                boolean remove = modelRealRelateService.remove(
                        new QueryWrapper<ModelRealRelate>()
                                .eq("model_id", modelform.getModel().getModelId())
                                .in("real_point_id", deleteSet)
                );
                if (!remove) {
                    throw new CrudException("修改模型失败");
                }
            }
        }
        if(!modelRealRelateList.isEmpty()){
            boolean saveBatch = modelRealRelateService.saveBatch(modelRealRelateList);
            if(!saveBatch){
                throw new CrudException("修改模型失败");
            }
        }
        List<Map<String,Object>> result = new ArrayList<>();
        //返回值(可以自行修改)
        for (Models models : modelsList) {
            Map<String,Object> map = new HashMap<>();
            map.put("modelId",models.getModelId());
            map.put("modelName",models.getModelName());
            map.put("modelVersion",models.getModelVersion());
            map.put("modelStatus",models.getModelStatus());
            map.put("turbinId",models.getTurbineId());
            result.add(map);
        }
        return EwsResult.OK("修改模型成功",result);
    }

    @DeleteMapping("/delete")
    @Transactional
    public EwsResult<?> deleteModel(@RequestBody List<Integer> ModelIdList){
        //删除model表
        boolean remove1 = modelsService.removeByIds(ModelIdList);
        if(!remove1){
            throw new CrudException("删除模型失败");
        }
        //删除model_real_relate表
        boolean remove2 = modelRealRelateService.remove(
                new QueryWrapper<ModelRealRelate>().in("model_id", ModelIdList)
        );
        if(!remove2){
            throw new CrudException("删除模型失败");
        }
        //TODO：删除模型文件
        return EwsResult.OK("删除模型成功");
    }

    @PostMapping("/train")
    public EwsResult<?> train(@RequestBody Map<String, Object> FileForm) {
        List<Integer> modelIds =(List<Integer>) FileForm.get("modelIds");
        List<Map<String, Object>> timePeriods = (List<Map<String, Object>>)FileForm.get("timePeriods");
        for (Map<String, Object> period : timePeriods) {
            LocalDateTime startTime = DateUtil.getLocalDateTime((String) period.get("startTime"));
            LocalDateTime endTime = DateUtil.getLocalDateTime((String) period.get("endTime"));

            //
        }
        List<String> standPoints = (List<String>) FileForm.get("standPoints");
        Map<String, String> standToRealPointMap = standToRealLabel(standPoints);
        // 参数校验
        if (standPoints == null || standPoints.isEmpty()) {
            return EwsResult.error("数据库名称缺失");
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
    //标准测点标签 -> 真实测点标签
    public Map<String, String> standToRealLabel(List<String> standpointList){
        Map<String, String> standToRealPointMap = new HashMap<>();
        //标准测点标签 -> 标准测点ID
        Map<String, Integer> standPointMap = standPointMapper.selectList(
                new QueryWrapper<StandPoint>().in("point_label", standpointList)
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
        for (String standPointLabel : standpointList) {
            Integer standPointId = standPointMap.get(standPointLabel);
            Integer realPointId = standToRealMap.get(standPointId);
            String realPointLabel = RealPointMap.get(realPointId);
            standToRealPointMap.put(standPointLabel, realPointLabel);
        }
        return standToRealPointMap;
    }
    //标准测点标签 -> 真实测点ID
    public Map<String, Integer> standToRealId(List<String> standpointList){
        Map<String, Integer> standToRealPointMap = new HashMap<>();
        //标准测点标签 -> 标准测点ID
        Map<String, Integer> standPointMap = standPointMapper.selectList(
                new QueryWrapper<StandPoint>().in("point_label", standpointList)
        ).stream().collect(Collectors.toMap(StandPoint::getPointLabel, StandPoint::getPointId));
        //标准测点ID -> 真实测点ID
        Map<Integer, Integer> standToRealMap = standRealRelateMapper.selectList(
                new QueryWrapper<StandRealRelate>().in("stand_point_id", standPointMap.values())
        ).stream().collect(Collectors.toMap(StandRealRelate::getStandPointId, StandRealRelate::getRealPointId));
        //标准测点标签 -> 真实测点标签
        for (String standPointLabel : standpointList) {
            Integer standPointId = standPointMap.get(standPointLabel);
            Integer realPointId = standToRealMap.get(standPointId);
            standToRealPointMap.put(standPointLabel, realPointId);
        }
        return standToRealPointMap;
    }
}
