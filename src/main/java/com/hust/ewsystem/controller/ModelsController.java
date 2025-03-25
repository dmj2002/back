package com.hust.ewsystem.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DTO.ModelChangeDTO;
import com.hust.ewsystem.DTO.ThresholdDTO;
import com.hust.ewsystem.VO.StandPointVO;
import com.hust.ewsystem.VO.ThresholdVO;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.*;
import com.hust.ewsystem.entity.Module;
import com.hust.ewsystem.mapper.*;
import com.hust.ewsystem.service.CommonDataService;
import com.hust.ewsystem.service.ModelRealRelateService;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


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

    @Autowired
    private AlgorithmsMapper algorithmsMapper;

    @Autowired
    private AlgorithmStandRelateMapper algorithmStandRelateMapper;

    @Autowired
    private WindFarmMapper windFarmMapper;

    @Autowired
    private WindTurbineMapper windTurbineMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private WarningService warningService;

    @Autowired
    private TaskMapper taskMapper;


    /**
     * 添加模型
     * @param modelform
     * @return
     */
    @PostMapping("/add")
    @Transactional
    public EwsResult<?> addmodel(@RequestBody ModelForm modelform){
        //models表插入
        List<Models> modelsList = new ArrayList<>();
        for (Integer turbineId : modelform.getTurbineList()) {
            //先写传入的模型参数
            Models newModel = new Models();
            newModel.setModelName(modelform.getModel().getModelName() + "_" + turbineId)
                    .setAlgorithmId(modelform.getModel().getAlgorithmId())
                    .setModelParameters(modelform.getModel().getModelParameters())
                    .setPatternId(modelform.getModel().getPatternId())
                    .setModuleId(modelform.getModel().getModuleId())
                    .setAlertInterval(modelform.getModel().getAlertInterval() != null ? modelform.getModel().getAlertInterval() : 10)
                    .setAlertWindowSize(modelform.getModel().getAlertWindowSize() != null ? modelform.getModel().getAlertWindowSize() : 60);
            //后端自己生成的模型参数
            newModel.setTurbineId(turbineId)
                    .setModelVersion("V1.0")
                    .setModelStatus(0);
            modelsList.add(newModel);
        }
        boolean saveBatch1 = modelsService.saveBatch(modelsList);
        if(!saveBatch1){
            throw new CrudException("模型批量保存失败");
        }
        for (Models models : modelsList) {
            //插入后才有modelId
            String modelLabel = "M" + String.format("%04d", models.getModelId());
            models.setModelLabel(modelLabel);
            File modelDir = new File(String.format("%s/%s", pythonFilePath, modelLabel));
            if (!modelDir.exists()) {
                if (!modelDir.mkdirs()) {
                    throw new FileException("创建文件目录失败");
                }
            }
        }
        boolean updateBatch = modelsService.updateBatchById(modelsList);
        if(!updateBatch){
            throw new CrudException("模型批量保存失败");
        }
        //model_real_relate表插入
        List<String> standpointList = modelform.getPointList();
        Map<String, List<Integer>> standToRealIdMap = standToRealId(standpointList);
        List<ModelRealRelate> modelRealRelateList = new ArrayList<>();

        for (String standPoint : standpointList) {
            // 获取真实测点 ID 列表
            List<Integer> realPointIds = standToRealIdMap.getOrDefault(standPoint, new ArrayList<>());
            // 遍历模型列表
            for (Models models : modelsList) {
                // 遍历每个真实测点 ID
                Integer uniqueRealId = findUniqueRealId(realPointIds, models.getTurbineId());
                if (uniqueRealId == null) {
                    continue; // 如果找不到唯一的真实 ID，跳过
                }
                // 创建关联对象并添加到列表
                ModelRealRelate modelRealRelate = new ModelRealRelate();
                modelRealRelate.setModelId(models.getModelId())
                        .setRealPointId(uniqueRealId);
                modelRealRelateList.add(modelRealRelate);
            }
        }

        boolean saveBatch2 = modelRealRelateService.saveBatch(modelRealRelateList);
        if(!saveBatch2){
            throw new CrudException("模型关联批量保存失败");
        }
        //返回值
        List<Map<String,Object>> result = new ArrayList<>();
        for (Models models : modelsList) {
            Map<String,Object> map = new HashMap<>();
            map.put("modelId",models.getModelId());
            map.put("modellabel",models.getModelLabel());
            map.put("modelName",models.getModelName());
            map.put("modelVersion",models.getModelVersion());
            map.put("modelStatus",models.getModelStatus());
            map.put("turbinId",models.getTurbineId());
            result.add(map);
        }
        return EwsResult.OK(result);
    }

    /**
     * 查询真实测点ID
     * @param realPointIds
     * @param turbineId
     * @return
     */
    private Integer findUniqueRealId(List<Integer> realPointIds, Integer turbineId) {
        // 查询 real_point 表，筛选出符合条件的唯一 realId
        return realPointMapper.selectOne(new QueryWrapper<RealPoint>()
                        .in("point_id", realPointIds) // 在 realPointIds 中查找
                        .eq("turbine_id", turbineId)       // 匹配 turbineId
        ).getPointId();
    }

    /**
     * 修改模型
     * @param modelChangeDTO
     * @return
     */
    @PostMapping("/change")
    @Transactional
    public EwsResult<?> changemodel(@RequestBody ModelChangeDTO modelChangeDTO){
        List<Models> modelsList = new ArrayList<>();
        modelChangeDTO.getModelIds().forEach(modelId -> {
            Models model = modelsService.getById(modelId);
            //TODO: 修改模型名称逻辑不确定对不对
            if(modelChangeDTO.getModelName() != null){
                String nameSuffix = model.getModelName().split("_")[1];
                model.setModelName(modelChangeDTO.getModelName() + "_" + nameSuffix);
            }
            if(modelChangeDTO.getAlertWindowSize() != null){
                model.setAlertWindowSize(modelChangeDTO.getAlertWindowSize());
            }
            if(modelChangeDTO.getAlertInterval() != null){
                model.setAlertInterval(modelChangeDTO.getAlertInterval());
            }
            String[] versionParts = model.getModelVersion().split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0].substring(1)); //去掉v
            int minorVersion = Integer.parseInt(versionParts[1]);
            if (minorVersion < 9) {
                minorVersion++;
            } else {
                majorVersion += 1;
                minorVersion = 0;
            }
            String newVersion = "V" + majorVersion + "." + minorVersion;
            //像版本号这种
            model.setModelVersion(newVersion);
            modelsList.add(model);
        });
        modelsService.updateBatchById(modelsList);
        return EwsResult.OK("修改模型成功");
    }
//    @PostMapping("/change")
//    @Transactional
//    public EwsResult<?> changemodel(@RequestBody List<ModelForm> model){
//        //models表修改
//        List<Models> modelsList = new ArrayList<>();
//        for(ModelForm modelform : model){
//            //传过来的参数直接修改
//            Models newModel = modelform.getModel();
////            String oldVersion = modelsService.getById(newModel.getModelId()).getModelVersion();
//            String[] versionParts = newModel.getModelVersion().split("\\.");
//            int majorVersion = Integer.parseInt(versionParts[0].substring(1)); //去掉v
//            int minorVersion = Integer.parseInt(versionParts[1]);
//            if (minorVersion < 9) {
//                minorVersion++;
//            } else {
//                majorVersion += 1;
//                minorVersion = 0;
//            }
//            String newVersion = "v" + majorVersion + "." + minorVersion;
//            //像版本号这种
//            newModel.setModelVersion(newVersion);
//            newModel.setModelStatus(0);
//            modelsList.add(newModel);
//        }
//        boolean updateBatch = modelsService.updateBatchById(modelsList);
//        if(!updateBatch){
//            throw new CrudException("修改模型失败");
//        }
//        //model_real_relate表修改(可能会变多可能会变少)
//        //两种方案，目前采用法二
//        //法一：先全部删除再插入
//        //法二：先查出所有的，然后对比，删除不需要的，插入需要的（通过集合的差进行判断）
//        List<ModelRealRelate> modelRealRelateList = new ArrayList<>();
//        for(ModelForm modelform : model){
//            List<String> standpointList = modelform.getPointList();
//            //表示当前模型没有任何测点
//            if(standpointList == null || standpointList.isEmpty()){
//                modelRealRelateService.remove(
//                        new QueryWrapper<ModelRealRelate>().eq("model_id", modelform.getModel().getModelId())
//                );
//                continue;
//            }
//            Map<String, List<Integer>> standToRealIdMap = standToRealId(standpointList);
//            // 将 Map 的值收集到 Set中: A
//            Set<Integer> realPointIdSet = new HashSet<>();
//            for(String standpoint:standpointList){
//                List<Integer> RealIds = standToRealIdMap.getOrDefault(standpoint, new ArrayList<>());
//                Integer realId = findUniqueRealId(RealIds, modelform.getModel().getTurbineId());
//                realPointIdSet.add(realId);
//            }
//            //当前模型的测点:B
//            Set<Integer> exitrealPointIdSet = modelRealRelateService.list(
//                    new QueryWrapper<ModelRealRelate>().eq("model_id", modelform.getModel().getModelId())
//                    ).stream().map(ModelRealRelate::getRealPointId).collect(Collectors.toSet());
//            //A - B = 需要插入的测点
//            Set<Integer> insertSet = realPointIdSet.stream()
//                    .filter(item -> !exitrealPointIdSet.contains(item))
//                    .collect(Collectors.toSet());
//            //B - A = 需要删除的测点
//            Set<Integer> deleteSet = exitrealPointIdSet.stream()
//                    .filter(item -> !realPointIdSet.contains(item))
//                    .collect(Collectors.toSet());
//            //插入(不需要主键，最后统一插入)
//            if(!insertSet.isEmpty()){
//                for(Integer realPointId : insertSet){
//                    ModelRealRelate modelRealRelate = new ModelRealRelate();
//                    modelRealRelate.setModelId(modelform.getModel().getModelId())
//                                   .setRealPointId(realPointId);
//                    modelRealRelateList.add(modelRealRelate);
//                }
//            }
//            //删除
//            if (!deleteSet.isEmpty()) {
//                boolean remove = modelRealRelateService.remove(
//                        new QueryWrapper<ModelRealRelate>()
//                                .eq("model_id", modelform.getModel().getModelId())
//                                .in("real_point_id", deleteSet)
//                );
//                if (!remove) {
//                    throw new CrudException("修改模型失败");
//                }
//            }
//        }
//        if(!modelRealRelateList.isEmpty()){
//            boolean saveBatch = modelRealRelateService.saveBatch(modelRealRelateList);
//            if(!saveBatch){
//                throw new CrudException("修改模型失败");
//            }
//        }
//        List<Map<String,Object>> result = new ArrayList<>();
//        //返回值(可以自行修改)
//        for (Models models : modelsList) {
//            Map<String,Object> map = new HashMap<>();
//            map.put("modelId",models.getModelId());
//            map.put("modelName",models.getModelName());
//            map.put("modelVersion",models.getModelVersion());
//            map.put("modelStatus",models.getModelStatus());
//            map.put("turbinId",models.getTurbineId());
//            result.add(map);
//        }
//        return EwsResult.OK("修改模型成功",result);
//    }

    @DeleteMapping("/delete")
    @Transactional
    public EwsResult<?> deleteModel(@RequestBody List<Integer> ModelIdList){
        //删除model_real_relate表
        boolean remove1 = modelRealRelateService.remove(
                new QueryWrapper<ModelRealRelate>().in("model_id", ModelIdList)
        );
        if(!remove1){
            throw new CrudException("删除模型失败");
        }
        //删除model表
        boolean remove2 = modelsService.removeByIds(ModelIdList);
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
        //返回值
        List<Map<String,Object>> taskIdList = new ArrayList<>();
        //传入的每个模型测点不一定相同，所以需要分别处理
        for(Integer modelId : modelIds){
            //修改模型状态为训练中
            modelsService.updateById(new Models().setModelStatus(1).setModelId(modelId));
            String modelLabel = modelsService.getById(modelId).getModelLabel();
            List<Integer> realpointId = modelRealRelateService.list(
                    new QueryWrapper<ModelRealRelate>().eq("model_id", modelId)
            ).stream().map(ModelRealRelate::getRealPointId).collect(Collectors.toList());
            //真实测点标签 -> 标准测点标签

            Map<String, String> realToStandLabel = RealToStandLabel(realpointId);
            // 查询数据并提取 datetime和value列
            Map<LocalDateTime, Map<String, Object>> alignedData = new TreeMap<>();
            for (Map<String, Object> period : timePeriods) {
                String startTime = (String) period.get("startTime");
                String endTime = (String) period.get("endTime");
                for (Map.Entry<String,String> entry : realToStandLabel.entrySet()) {
                    List<CommonData> data = commonDataService.selectDataByTime(entry.getKey().toLowerCase(), startTime, endTime);
                    for (CommonData record : data) {
                        LocalDateTime datetime = record.getDatetime();
                        Double value = record.getValue();
                        alignedData.computeIfAbsent(datetime, k -> new HashMap<>()).put(entry.getValue(), value);
                    }
                }
            }
            // 写入CSV文件
            toTrainCsv(alignedData, realToStandLabel, modelLabel);
            Integer algorithmId = modelsService.getById(modelId).getAlgorithmId();
            String algorithmLabel = algorithmsMapper.selectById(algorithmId).getAlgorithmLabel();
            // 算法调用
            String taskId = modelsService.train(algorithmLabel, modelLabel,modelId);

            Map<String,Object> map= new HashMap<>();
            map.put("modelId",modelId);
            map.put("taskId",taskId);
            taskIdList.add(map);
        }
        return EwsResult.OK(taskIdList);
    }

    @PostMapping("/predict")
    public EwsResult<?> predict(@RequestBody List<Integer> modelList){
//        List<Map<String,Object>> taskIdList = new ArrayList<>();
        for(Integer modelId : modelList) {
            //获取返回值
            Models model = modelsService.getById(modelId);
            Integer alertInterval = model.getAlertInterval();
            String modelLabel = model.getModelLabel();
            Integer algorithmId = model.getAlgorithmId();
            Integer alertWindowSize = model.getAlertWindowSize();
            String algorithmLabel = algorithmsMapper.selectById(algorithmId).getAlgorithmLabel();
            //算法调用
            modelsService.predict(alertInterval, modelLabel, algorithmLabel, modelId,alertWindowSize);
//            Map<String,Object> map= new HashMap<>();
//            map.put("modelId",modelId);
//            taskIdList.add(map);
            //修改模型状态为预测中
            UpdateWrapper<Models> modelsUpdateWrapper = new UpdateWrapper<>();
            modelsUpdateWrapper.eq("model_id", modelId).set("model_status", 3);
            modelsService.update(modelsUpdateWrapper);
        }
        return EwsResult.OK("模型开始预测");
    }
    @PostMapping("/test")
    public EwsResult<?> testModel(@RequestBody Map<String, Object> FileForm){
        Integer modelId = (Integer) FileForm.get("modelId");
        String startTime = (String)FileForm.get("startTime");
        String endTime = (String)FileForm.get("endTime");
        //删除对应时间段的所有预警重新生成
        warningService.remove(new QueryWrapper<Warnings>().eq("model_id", modelId).ge("start_time", startTime).le("end_time", endTime));
        //获取返回值
        Models model = modelsService.getById(modelId);
        Integer alertInterval = model.getAlertInterval();
        String modelLabel = model.getModelLabel();
        Integer algorithmId = model.getAlgorithmId();
        Integer alertWindowSize = model.getAlertWindowSize();
        String algorithmLabel = algorithmsMapper.selectById(algorithmId).getAlgorithmLabel();
        //算法调用
        modelsService.testPredict(alertInterval, modelLabel, algorithmLabel, modelId,alertWindowSize,startTime,endTime);
        return EwsResult.OK("模型开始测试");
    }

    @PostMapping("/testZip")
    public EwsResult<?> testModelZip(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("modelId") Integer modelId){
        if(file.isEmpty()){
            return EwsResult.error("文件为空");
        }
        String label = UUID.randomUUID().toString();
        File tempDir = new File(String.format("%s/temp_%s/", pythonFilePath, label));
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        // 解压 ZIP 文件到临时文件夹
        try {
            unzip(file.getInputStream(), tempDir);

            // 获取所有 CSV 文件
            File[] files = tempDir.listFiles((dir, name) -> name.endsWith(".csv"));
            if (files == null || files.length == 0) {
                return EwsResult.error("文件为空");
            }
            // 创建一个临时目录用于存储预测结果
            File resultDir = new File(tempDir + "/result");
            if (!resultDir.exists()) {
                resultDir.mkdirs();
            }

            // 对每个 CSV 文件进行模型预测，并保存结果
            Models model = modelsService.getById(modelId);
            String algorithmLabel = "A" + String.format("%04d", model.getAlgorithmId());
            String modelLabel = model.getModelLabel();
            runModel(resultDir,files, modelLabel, algorithmLabel,label);
            return EwsResult.OK("模型开始测试",label);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return EwsResult.error("文件解压失败");
        }
    }
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam("label") String label) throws IOException {
        tasks nowTask = taskMapper.selectOne(new QueryWrapper<tasks>().eq("task_label", label));
        if(nowTask == null || nowTask.getTaskType() != 2){
            return ResponseEntity.notFound().build();
        }
        // 打包所有结果文件为一个新的 ZIP 文件
        File tempDir = new File(String.format("%s/temp_%s/", pythonFilePath, label));
        File resultDir = new File(tempDir, "result");
        File zipFile = new File(resultDir , "result.zip");
        zipDirectory(resultDir, zipFile);
        File resultFile = new File(pythonFilePath + "/temp_" + label + "/result.zip");
        if (!resultFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(resultFile));
        // 删除临时文件夹或者不删除
//        deleteDirectory(tempDir);
        modelsService.deleteTask(label,"test");
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"result.zip\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resultFile.length())
                .body(resource);
    }

    @GetMapping("/showThreshold")
    public EwsResult<?> showThreshold(@RequestParam("modelId") Integer modelId){
        Models model = modelsService.getById(modelId);
        String modelLabel = model.getModelLabel();
        List<ThresholdVO> thresholdList = modelsService.showThreshold(modelLabel);
        return EwsResult.OK(thresholdList);
    }

    @PostMapping("/changeThreshold")
    public EwsResult<?> changeThreshold(@RequestBody ThresholdDTO thresholdDTO){
        Integer modelId = thresholdDTO.getModelId();
        String modelLabel = modelsService.getById(modelId).getModelLabel();
        List<ThresholdVO> items = thresholdDTO.getItems();
        File resultFile = new File(pythonFilePath + modelLabel + "/new_model.json");
        // 尝试创建文件或覆盖已有文件
        try {
            if (resultFile.exists()) {
                boolean deleted = resultFile.delete();
                if (!deleted) {
                    return EwsResult.error("修改阈值失败"); // 如果无法删除文件，结束方法
                }
            }
            // 创建新文件
            boolean fileCreated = resultFile.createNewFile();
            if (fileCreated) {
                System.out.println("文件创建成功: " + resultFile.getAbsolutePath());
                //写入结果到文件
                writeFileContent(resultFile.getAbsoluteFile(), items);
            } else {
                return EwsResult.error("修改阈值失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EwsResult.OK("修改阈值成功");
    }

    @GetMapping("/list")
    public EwsResult<?> listModel(@RequestParam(value = "page") int page,
                                  @RequestParam(value = "page_size") int pageSize,
                                  @RequestParam(value = "module_id", required = false) Integer moduleId,
                                  @RequestParam(value = "company_id", required = false) Integer companyId,
                                  @RequestParam(value = "windfarm_id", required = false) Integer windfarmId,
                                  @RequestParam(value = "algorithm_id", required = false) Integer algorithmId,
                                  @RequestParam(value = "turbine_id", required = false) Integer turbineId) {
        Page<Models> modelsPage = new Page<>(page, pageSize);
        QueryWrapper<Models> queryWrapper = new QueryWrapper<>();
        if (moduleId != null) {
            queryWrapper.eq("module_id", moduleId);
        }
        if(algorithmId != null){
            queryWrapper.eq("algorithm_id", algorithmId);
        }
        if (turbineId != null) {
            queryWrapper.eq("turbine_id", turbineId);
        }
        else{
            List<Integer> turbineList = new ArrayList<>();
            if (companyId != null) {
                if (windfarmId != null) {
                    turbineList = windTurbineMapper.selectList(
                            new QueryWrapper<WindTurbine>().eq("wind_farm_id", windfarmId)
                    ).stream().map(WindTurbine::getTurbineId).collect(Collectors.toList());
                }
                else {
                    List<Integer> windfarmList = windFarmMapper.selectList(
                            new QueryWrapper<WindFarm>().eq("company_id", companyId)
                    ).stream().map(WindFarm::getWindFarmId).collect(Collectors.toList());
                    turbineList = windTurbineMapper.selectList(
                            new QueryWrapper<WindTurbine>().in("wind_farm_id", windfarmList)
                    ).stream().map(WindTurbine::getTurbineId).collect(Collectors.toList());
                }
            }
            if (!turbineList.isEmpty()) {
                queryWrapper.in("turbine_id", turbineList);
            }
        }
        Page<Models> page1 = modelsService.page(modelsPage, queryWrapper);
        if (page1.getRecords().isEmpty() || page1.getRecords() == null) {
            return EwsResult.error("查询结果为空");
        }
        List<Models> records = page1.getRecords();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Models model : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("modelId", model.getModelId());
            map.put("modelLabel", model.getModelLabel());
            map.put("modelName", model.getModelName());
            map.put("modelVersion", model.getModelVersion());
            map.put("turbineId", model.getTurbineId());
            map.put("turbineName", windTurbineMapper.selectById(model.getTurbineId()).getTurbineName());
            map.put("algorithmId", model.getAlgorithmId());
            map.put("algorithmName",algorithmsMapper.selectById(model.getAlgorithmId()).getAlgorithmName());
            map.put("moduleId", model.getModuleId());
//            map.put("moduleName",moduleMapper.selectById(model.getModuleId()).getModuleName());
            map.put("modelStatus", model.getModelStatus());
            result.add(map);
        }
//        QueryWrapper<WindTurbine> windTurbineQueryWrapper = new QueryWrapper<>();
//        windTurbineQueryWrapper.select("turbine_id", "turbine_type","turbine_name","wind_farm_id");  // 指定你需要的字段
//        List<WindTurbine> turbineList = windTurbineMapper.selectList(windTurbineQueryWrapper);
//
//
//        QueryWrapper<WindFarm> windFarmQueryWrapper = new QueryWrapper<>();
//        windFarmQueryWrapper.select("wind_farm_id", "wind_farm_name,company_id");
//        List<WindFarm> windFarmList = windFarmMapper.selectList(windFarmQueryWrapper);
//
//        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
//        companyQueryWrapper.select("company_id", "company_name");
//        List<Company> companyList = companyMapper.selectList(companyQueryWrapper);
//
//        QueryWrapper<Module> moduleQueryWrapper = new QueryWrapper<>();
//        moduleQueryWrapper.select("module_id", "module_name");
//        List<Module> moduleList = moduleMapper.selectList(moduleQueryWrapper);
//
//        QueryWrapper<Algorithms> algorithmsQueryWrapper = new QueryWrapper<>();
//        algorithmsQueryWrapper.select("algorithm_id","algorithm_name","algorithm_label");
//        List<Algorithms> algorithmsList = algorithmsMapper.selectList(algorithmsQueryWrapper);
//
//        QueryWrapper<StandPoint> standPointQueryWrapper = new QueryWrapper<>();
//        standPointQueryWrapper.select("point_id","point_label","point_description");
//        List<StandPoint> standPointList = standPointMapper.selectList(standPointQueryWrapper);
//
//        QueryWrapper<AlgorithmStandRelate> algorithmStandRelateQueryWrapper = new QueryWrapper<>();
//        algorithmStandRelateQueryWrapper.select("algorithm_id","stand_point_id");
//        List<AlgorithmStandRelate> algorithmStandRelateList = algorithmStandRelateMapper.selectList(algorithmStandRelateQueryWrapper);

        Map<String,Object> response = new HashMap<>();
        response.put("total_count",page1.getTotal());
        response.put("page",page1.getCurrent());
        response.put("page_size",page1.getSize());
        response.put("total_pages",page1.getPages());
        response.put("modelList",result);
//        response.put("companyList",companyList);
//        response.put("windFarmList",windFarmList);
//        response.put("turbineList",turbineList);
//        response.put("moduleList",moduleList);
//        response.put("algorithmList",algorithmsList);
//        response.put("standPointList",standPointList);
//        response.put("algorithmStandRelateList",algorithmStandRelateList);
        return EwsResult.OK("查询成功", response);
    }
//    // 查询任务状态
//    @PostMapping("/queryTask")
//    public EwsResult<?>  getTaskStatus(@RequestBody Map<String,Object> taskForm) {
//        List<String> taskIdList = (List<String>) taskForm.get("taskIdList");
//        List<Map<String, Object>> taskStatus = new ArrayList<>();
//        // 检查任务ID列表是否为空
//        if (taskIdList == null || taskIdList.isEmpty()) {
//            return EwsResult.error("任务ID列表不能为空");
//        }
//        for (String taskLabel : taskIdList) {
//            Map<String, Object> onetaskStatus = modelsService.getTaskStatus(taskLabel);
//            taskStatus.add(onetaskStatus);
//        }
//        return EwsResult.OK(taskStatus);
//    }

    /**
     * 根据模型id暂停任务
     * @param modelIdList
     * @return
     */
    @PostMapping("/stopPredict")
    public EwsResult<?> stopPredict(@RequestBody List<Integer> modelIdList){
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Integer modelId : modelIdList){
            Map<String, Object> result = new HashMap<>();
            Models model = modelsService.getById(modelId);
            //修改模型状态为已完成
            model.setModelStatus(2);
            modelsService.updateById(model);
            String str = modelsService.killTask(model.getModelLabel());

            result.put("modelId", modelId);
            result.put("result", str);
            resultList.add(result);
        }
        return EwsResult.OK(resultList);
    }
    @GetMapping("getStandPoint/{AlgorithmId}")
    public EwsResult<?> getStandPoint(@PathVariable("AlgorithmId") Integer algorithmId) {
        List<StandPointVO> standPointByAlgorithmId = algorithmStandRelateMapper.getStandPointByAlgorithmId(algorithmId);
        return EwsResult.OK(standPointByAlgorithmId);
    }
    @GetMapping("getModelStatus/{modelId}")
    public EwsResult<?> getModelStatus(@PathVariable("modelId") Integer modelId) {
        Integer modelStatus = modelsService.getById(modelId).getModelStatus();
        switch(modelStatus){
            case 0:
                return EwsResult.OK("模型状态：未训练");
            case 1:
                return EwsResult.OK("模型状态：训练中");
            case 2:
                return EwsResult.OK("模型状态：训练成功带开启");
            case 3:
                return EwsResult.OK("模型状态：预测中");
            case 4:
                return EwsResult.OK("模型状态：训练失败");
            default:
                return EwsResult.OK("模型状态：未知");
        }
    }
    public void toTrainCsv(Map<LocalDateTime, Map<String, Object>> alignedData,Map<String, String> realToStandLabel,String modelLabel){
        // 创建目标目录（如果不存在）
        File modelDir = new File(String.format("%s/%s", pythonFilePath, modelLabel));
        if (!modelDir.exists()) {
            if (!modelDir.mkdirs()) {
                throw new FileException("创建文件目录失败");
            }
        }
        // 写入 CSV 文件
        try (FileWriter csvWriter = new FileWriter(String.format("%s/%s/train.csv", pythonFilePath, modelLabel))) {
            // 写入表头
            csvWriter.append("datetime");
            for (String standPoint : realToStandLabel.values()) {
                csvWriter.append(",").append(standPoint);
            }
            csvWriter.append("\n");
            // 写入数据
            for (Map.Entry<LocalDateTime, Map<String, Object>> entry : alignedData.entrySet()) {
                StringBuilder line = new StringBuilder(entry.getKey().toString());
                for (String standPoint : realToStandLabel.values()) {
                    Double value = (Double) entry.getValue().get(standPoint);
                    line.append(",").append(value);
                }
                csvWriter.append(line.toString()).append("\n");
            }
        } catch (IOException e) {
            throw new FileException("写入CSV文件失败", e);
        }
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
    //根据真实测点id查询标准测点标签和真实测点标签
    public Map<String, String> RealToStandLabel(List<Integer> realpointList){
        Map<String, String> RealTostandPointMap = new HashMap<>();
        // 真实测点ID->标准测点ID
        Map<Integer, Integer> standToRealMap = standRealRelateMapper.selectList(
                new QueryWrapper<StandRealRelate>().in("real_point_id", realpointList)
        ).stream().collect(Collectors.toMap(StandRealRelate::getRealPointId, StandRealRelate::getStandPointId));
        //真实测点ID -> 真实测点标签
        Map<Integer,String> RealPointMap = realPointMapper.selectList(
                new QueryWrapper<RealPoint>().in("point_id", standToRealMap.keySet())
        ).stream().collect(Collectors.toMap(RealPoint::getPointId, RealPoint::getPointLabel));
        //标准测点ID->标准测点标签
        Map<Integer, String> standPointMap = standPointMapper.selectList(
                new QueryWrapper<StandPoint>().in("point_id", standToRealMap.values())
        ).stream().collect(Collectors.toMap(StandPoint::getPointId, StandPoint::getPointLabel));
        //真实测点标签 -> 标准测点标签
        for (Integer realpointId : realpointList) {
            Integer standPointId = standToRealMap.get(realpointId);
            String realPointLabel = RealPointMap.get(realpointId);
            String standPointLabel = standPointMap.get(standPointId);
            RealTostandPointMap.put(realPointLabel, standPointLabel);
        }
        return RealTostandPointMap;
    }
    //标准测点标签 -> 真实测点IDs
    public Map<String, List<Integer>> standToRealId(List<String> standpointList) {
        // 修改返回值类型为 Map<String, List<Integer>>
        Map<String, List<Integer>> standToRealPointMap = new HashMap<>();

        // 标准测点标签 -> 标准测点ID
        Map<String, Integer> standPointMap = standPointMapper.selectList(
                new QueryWrapper<StandPoint>().in("point_label", standpointList)
        ).stream().collect(Collectors.toMap(StandPoint::getPointLabel, StandPoint::getPointId));

        // 标准测点ID -> 真实测点ID（支持一对多）
        Map<Integer, List<Integer>> standToRealMap = standRealRelateMapper.selectList(
                new QueryWrapper<StandRealRelate>().in("stand_point_id", standPointMap.values())
        ).stream().collect(Collectors.groupingBy(
                StandRealRelate::getStandPointId, // 分组键为 stand_point_id
                Collectors.mapping(StandRealRelate::getRealPointId, Collectors.toList()) // 值为 real_point_id 列表
        ));
        // 将标准测点标签 -> 真实测点ID 映射到最终结果
        for (String standPointLabel : standpointList) {
            Integer standPointId = standPointMap.get(standPointLabel);
            List<Integer> realPointIds = standToRealMap.getOrDefault(standPointId, new ArrayList<>());
            standToRealPointMap.put(standPointLabel, realPointIds);
        }
        return standToRealPointMap;
    }
    public static void writeFileContent(File file, List<ThresholdVO> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String content = JSON.toJSONString(items); // 转换为 JSON 字符串
            writer.write(content);  // 写入内容
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 解压 ZIP 文件
    private void unzip(InputStream zipInputStream, File targetDir) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                File file = new File(targetDir, entry.getName());
//                if (entry.isDirectory()) {
//                    file.mkdirs();
//                } else {
//                    try (FileOutputStream fos = new FileOutputStream(file)) {
//                        byte[] buffer = new byte[1024];
//                        int length;
//                        while ((length = zipIn.read(buffer)) != -1) {
//                            fos.write(buffer, 0, length);
//                        }
//                    }
//                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zipIn.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                    }
                }
                zipIn.closeEntry();
            }
        }
    }
    // 选择csv并将模型运行结果写到json
    private void runModel(File resultDir,File[] csvFile,String modelLabel,String algorithmLabel,String label)  {
        modelsService.zipPredict(modelLabel,algorithmLabel, resultDir, csvFile,label);
    }
    // 打包目录为 ZIP 文件
    private void zipDirectory(File sourceDir, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (File file : sourceDir.listFiles()) {
                if (file.isDirectory()) continue;
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }
    // 删除临时目录
    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}
