package com.hust.ewsystem.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.entity.*;
import com.hust.ewsystem.mapper.*;
import com.hust.ewsystem.service.CommonDataService;
import com.hust.ewsystem.service.ModelRealRelateService;
import com.hust.ewsystem.service.ModelsService;

import com.hust.ewsystem.service.WarningService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelsServiceImpl.class);

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;
    @Value("${algorithm.threshold}")
    public Double threshold;

    @Autowired
    private WarningService warningService;
    @Autowired
    private TaskMapper tasksMapper;
    @Autowired
    private ModelRealRelateService modelRealRelateService;
    @Autowired
    private StandRealRelateMapper standRealRelateMapper;
    @Autowired
    private StandPointMapper standPointMapper;
    @Autowired
    private RealPointMapper realPointMapper;
    @Autowired
    private CommonDataService commonDataService;
    // 任务状态
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(64);
    @Override
    public String train(String algorithmLabel, String modelLabel,Integer modelId) {
        String taskLabel = UUID.randomUUID().toString();
        tasks newtask = new tasks();
        newtask.setModelId(modelId)
                .setTaskType(0)
                .setTaskLabel(taskLabel)
                .setStartTime(LocalDateTime.now());
        tasksMapper.insert(newtask);
        File taskDir = new File(pythonFilePath + "/task_logs/" + taskLabel);
        if (!taskDir.exists()) {
            if (!taskDir.mkdirs()) {
                throw new FileException("创建任务目录失败");
            }
        }
        //准备setting.json
        File settingFile = new File(taskDir, "setting.json");
        JSONObject settings = new JSONObject();
        settings.put("modelPath", pythonFilePath + "/" + modelLabel);
        settings.put("trainDataPath", pythonFilePath + "/" + modelLabel + "/train.csv");
        settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/predict.csv");
        settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/result.json");
        settings.put("logPath", pythonFilePath + "/task_logs/" + taskLabel + "/" + taskLabel + ".log");
        // 写入 setting.json 文件
        try (FileWriter fileWriter = new FileWriter(settingFile)) {
            fileWriter.write(settings.toJSONString());
        } catch (IOException e) {
            throw new FileException("setting.json文件配置失败",e);
        }
        Runnable task = () -> executeTrain(pythonFilePath, algorithmLabel, taskLabel, modelId);
        // 调度任务一次性执行
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, 0, TimeUnit.SECONDS);
        taskMap.put(modelLabel + "_train", scheduledTask);
        return taskLabel;
    }
    @Override
    public void predict(Integer alertInterval, String modelLabel, String algorithmLabel,Integer modelId,Integer alertWindowSize) {
        Runnable task = () ->{
            try {
                prePredict(modelId,modelLabel,algorithmLabel,alertWindowSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // 定期调度任务
        ScheduledFuture<?> scheduledTask =scheduler.scheduleWithFixedDelay(task, 0, alertInterval, TimeUnit.SECONDS);
        taskMap.put(modelLabel + "_predict", scheduledTask);
    }

//    // 提供查询任务状态的接口
//    @Override
//    public Map<String, Object> getTaskStatus(String taskLabel) {
//        Map<String, Object> statusMap = new HashMap<>();
//        statusMap.put("taskLabel", taskLabel);
//        ScheduledFuture<?> scheduledTask = taskMap.get(taskLabel);
//        if (scheduledTask == null) {
//            statusMap.put("status", "任务不存在");
//        } else if (scheduledTask.isCancelled()) {
//            statusMap.put("status", "任务已取消");
//        } else if (scheduledTask.isDone()) {
//            statusMap.put("status", "任务已完成");
//        } else {
//            statusMap.put("status", "任务进行中");
//        }
//        return statusMap;
//    }

    /**
     * 终止任务
     * @param modelLabel
     * @return
     */
    @Override
    public String killTask(String modelLabel) {
        // 终止ScheduledFuture任务
        ScheduledFuture<?> scheduledTask = taskMap.get(modelLabel + "_predict");
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            taskMap.remove(modelLabel + "_predict");
        }
        // 检查任务和线程是否都已终止
        if (scheduledTask == null) {
            return "任务不存在";
        } else {
            return "任务已终止";
        }
    }

    /**
     *
     * @param filepath 进程工作目录
     * @param algorithmLabel 算法标签
     * @param taskLabel 任务ID
     */
    public void executeTrain(String filepath, String algorithmLabel, String taskLabel, Integer modelId) {
        Process process = null;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python3");
            command.add(String.format("alg/%s/train.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskLabel));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for task: " + taskLabel);
            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Python process failed with exit code: " + exitCode);
                //修改模型状态为训练失败
                UpdateWrapper<Models> modelsUpdateWrapper = new UpdateWrapper<>();
                modelsUpdateWrapper.eq("model_id", modelId).set("model_status", 3);
                update(modelsUpdateWrapper);
            } else {
                System.out.println("Python process completed successfully for task: " + taskLabel);
                //修改模型状态为训练成功
                UpdateWrapper<Models> modelsUpdateWrapper = new UpdateWrapper<>();
                modelsUpdateWrapper.eq("model_id", modelId).set("model_status", 2);
                update(modelsUpdateWrapper);
            }
        } catch(InterruptedException e) {
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void prePredict(int modelId, String modelLabel, String algorithmLabel,Integer alertWindowSize) {
        try {
            String taskLabel = UUID.randomUUID().toString();
            tasks newtask = new tasks();
            newtask.setModelId(modelId)
                    .setTaskType(1)
                    .setTaskLabel(taskLabel)
                    .setStartTime(LocalDateTime.now());
            tasksMapper.insert(newtask);
            Integer taskId= newtask.getTaskId();
            File taskDir = new File(pythonFilePath + "/task_logs/" + taskLabel);
            if (!taskDir.exists()) {
                if (!taskDir.mkdirs()) {
                    throw new FileException("创建任务目录失败");
                }
            }
            //TODO 读取实时数据
//            String trainFilePath = pythonFilePath + "/" + modelLabel + "/train.csv"; // 训练数据路径
//            String predictFilePath = taskDir.getAbsolutePath() + "/predict.csv"; // 预测文件路径
            List<Integer> realpointId = modelRealRelateService.list(
                    new QueryWrapper<ModelRealRelate>().eq("model_id", modelId)
            ).stream().map(ModelRealRelate::getRealPointId).collect(Collectors.toList());
            //真实测点标签 -> 标准测点标签
            Map<String, String> realToStandLabel = RealToStandLabel(realpointId);
            Map<LocalDateTime, Map<String, Object>> alignedData = new TreeMap<>();
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 计算结束时间 (当前时间 - 10 分钟)
            LocalDateTime endTime = now.minusMinutes(10);
            // 计算开始时间 (当前时间 - 10 分钟 - window 秒)
            LocalDateTime startTime = now.minusMinutes(10).minusSeconds(alertWindowSize);
            // 定义时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 将 LocalDateTime 转换为 String 格式
            String startTimeStr = startTime.format(formatter);
            String endTimeStr = endTime.format(formatter);
            for (Map.Entry<String,String> entry : realToStandLabel.entrySet()) {
                List<CommonData> data = commonDataService.selectDataByTime(entry.getKey().toLowerCase(), startTimeStr, endTimeStr);
                for (CommonData record : data) {
                    LocalDateTime datetime = record.getDatetime();
                    Double value = record.getValue();
                    Integer status = record.getStatus();
                    alignedData.computeIfAbsent(datetime, k -> new HashMap<>()).put(entry.getValue(), value);
                    alignedData.get(datetime).put(entry.getValue() + "_status", status);
                }
            }
            int sizeBeforeRemoval = alignedData.size();
            // 移除 GridPower 小于等于 0 的数据
            alignedData.entrySet().removeIf(entry -> {
                Map<String, Object> labelMap = entry.getValue(); // 获取每个时间点的标签数据
                // 如果 "Grid" 列的值小于或等于 0，移除该时间点的数据
                return labelMap.containsKey("GridPower") && (Double)labelMap.get("GridPower") <= 0;
            });
            // 获取移除后的大小
            int sizeAfterRemoval = alignedData.size();
            // 判断是否有数据被移除
            if(sizeBeforeRemoval > sizeAfterRemoval){
                LOGGER.info("model: " +modelId+ " and task: " + taskLabel + "的数据有异常，取消此次预测任务");
                return;
            }
            boolean res = toPredictCsv(alignedData, realToStandLabel, taskLabel);
            if(!res)return;
            //准备setting.json
            File settingFile = new File(taskDir, "setting.json");
            JSONObject settings = new JSONObject();
            settings.put("modelPath", pythonFilePath + "/" + modelLabel);
            settings.put("trainDataPath", pythonFilePath + "/" + modelLabel + "/train.csv");
            settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/predict.csv");
            settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/result.json");
            settings.put("logPath", pythonFilePath + "/task_logs/" + taskLabel + "/" + taskLabel + ".log");
            // 写入 setting.json 文件
            try (FileWriter fileWriter = new FileWriter(settingFile)) {
                fileWriter.write(settings.toJSONString());
            } catch (IOException e) {
                throw new FileException("setting.json文件配置失败",e);
            }
            executePredict(pythonFilePath, algorithmLabel, taskLabel, modelId, taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void executePredict(String filepath, String algorithmLabel, String taskLabel,Integer modelId,Integer taskId) {
        Process process = null;
        boolean interrupted = false;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add(String.format("alg/%s/predict.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskLabel));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for model: " +modelId+ " and task: " + taskLabel);
            StringBuilder outputString = null;
            //获取输入流
            InputStream inputStream = process.getInputStream();
            //转成字符输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            int len = -1;
            char[] c = new char[2048];
            outputString = new StringBuilder();
            //读取进程输入流中的内容
            while ((len = inputStreamReader.read(c)) != -1) {
                String s = new String(c, 0, len);
                outputString.append(s);
            }
            LOGGER.debug("算法执行结果：{}", outputString);
            inputStream.close();
            inputStreamReader.close();
            // 等待进程完成
            process.waitFor();
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                System.out.println("进程正常结束");
            } else {
                System.out.println("进程异常结束");
            }
        } catch (InterruptedException e) {
            interrupted = true;  // 记录中断状态
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!interrupted) {
                readAndSaveResults(filepath, taskLabel, modelId, taskId);
                LOGGER.info("Finished reading and saving results for task: " + taskLabel);
            }
        }
    }
    private void readAndSaveResults(String filepath, String taskLabel,Integer modelId,Integer taskId) {
        try {
            String resultFilePath = filepath + "/task_logs/" + taskLabel + "/result.json";

            // 强制使用 UTF-8 编码读取文件内容
            Path path = Paths.get(resultFilePath);              // 2025.1.21  判断文件是否存在
            if (Files.exists(path)) {
                StringBuilder contentBuilder = new StringBuilder();
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line);
                    }
                }
                String content = contentBuilder.toString();

                // 解析 JSON 内容
                JSONObject jsonObject = JSONObject.parseObject(content);
                JSONArray alertList = jsonObject.getJSONArray("alarm_list");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                List<JSONObject> alertJsonList = new ArrayList<>();
                for (int i = 0; i < alertList.size(); i++) {
                    alertJsonList.add(alertList.getJSONObject(i));
                }

                // 预警信息入库及合并
                processAlerts(alertJsonList, modelId, taskId, formatter);

//            for (int i = 0; i < alertList.size(); i++) {
//                JSONObject alert = alertList.getJSONObject(i);
//                String alertInfo = alert.getString("alarm_info");
//                LocalDateTime startTime = LocalDateTime.parse(alert.getString("start_time"), formatter);
//                LocalDateTime endTime = LocalDateTime.parse(alert.getString("end_time"), formatter);
//
//                // 保存到数据库
//                Warnings warning = new Warnings();
//                warning.setModelId(modelId);
//                warning.setWarningDescription(alertInfo);
//                warning.setStartTime(startTime);
//                warning.setEndTime(endTime);
//                warning.setTaskId(taskId);
//                warning.setWarningStatus(0);//异常状态：未处理
//                warning.setWarningLevel(0);//set为一级先
//                warningService.save(warning);
//            }
            }
            else{
                System.out.println("文件不存在: " + resultFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 预警信息入库及合并
     * @param alertList alertList
     * @param modelId modelId
     * @param taskId taskId
     * @param formatter formatter
     */
    private void processAlerts(List<JSONObject> alertList, int modelId, int taskId,DateTimeFormatter formatter) {
        Iterator<JSONObject> iterator = alertList.iterator();
        while (iterator.hasNext()) {
            JSONObject alert = iterator.next();
            String alertInfo = alert.getString("alarm_info");
            if(alertInfo.contains("正常")){
                continue;
            }
            LocalDateTime startTime = LocalDateTime.parse(alert.getString("start_time"), formatter);
            LocalDateTime endTime = LocalDateTime.parse(alert.getString("end_time"), formatter);
            //TODO 预警等级暂不知result返回的是int还是string
            String warningLevelStr = alert.getString("warning_level");
            Integer warningLevel;
            if (warningLevelStr != null && !warningLevelStr.isEmpty()) {
                warningLevel = Integer.parseInt(warningLevelStr);  // 转换为整数
            } else {
                warningLevel = 0;  // 如果为空或 null，则返回默认值 0
            }
            // 保存到数据库
            Warnings warning = new Warnings();
            warning.setModelId(modelId);
            warning.setWarningDescription(alertInfo);
            warning.setStartTime(startTime);
            warning.setEndTime(endTime);
            warning.setTaskId(taskId);
            warning.setWarningStatus(0);
            warning.setWarningLevel(warningLevel);

            LambdaQueryWrapper<Warnings> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Warnings::getModelId,modelId)
                        .eq(Warnings::getWarningLevel,warningLevel)
                        .eq(Warnings::getWarningDescription,alertInfo)
                        .ge(Warnings::getEndTime,startTime)
                        .le(Warnings::getStartTime,startTime);
            try {
                Warnings one = warningService.getOne(queryWrapper);
                if(one == null) warningService.save(warning);
                else{
                    one.setEndTime(endTime);
                    one.setWarningStatus(0);
                    warningService.updateById(one);
                }
            } catch (TooManyResultsException e) {
                LOGGER.error("Too many results returned: ", e);
            }
        }
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
    public boolean toPredictCsv(Map<LocalDateTime, Map<String, Object>> alignedData,Map<String, String> realToStandLabel,String taskLabel) {
        // 创建目标目录（如果不存在）
        File modelDir = new File(String.format("%s/task_logs/%s", pythonFilePath,taskLabel));
        if (!modelDir.exists()) {
            if (!modelDir.mkdirs()) {
                throw new FileException("创建文件目录失败");
            }
        }
        int totalSize = 0;
        int validSize = 0;
        for (Map.Entry<LocalDateTime, Map<String, Object>> entry : alignedData.entrySet()) {
            totalSize++;
            boolean allStatusValid = true;
            for(String standPoint : realToStandLabel.values()){
                Integer status = (Integer) entry.getValue().get(standPoint + "_status");
                if (status == null || status == 0) {
                    allStatusValid = false;
                    break;
                }
            }
            if (allStatusValid) {
                validSize++;
            }
        }
        double validRatio = (double) validSize / totalSize;
        //阈值，可以根据实际情况调整
        if(validRatio < threshold){
            LOGGER.info("model: " + taskLabel + "的数据有异常，取消此次预测任务");
            return false;
        }
        // 写入 CSV 文件
        try (FileWriter csvWriter = new FileWriter(String.format("%s/task_logs/%s/predict.csv", pythonFilePath, taskLabel))) {
            // 写入表头
            csvWriter.append("datetime");
            for (String standPoint : realToStandLabel.values()) {
                csvWriter.append(",").append(standPoint);
            }
            csvWriter.append("\n");
            // 写入数据
            for (Map.Entry<LocalDateTime, Map<String, Object>> entry : alignedData.entrySet()) {
                boolean isValid = true;
                for(String standPoint : realToStandLabel.values()){
                    Integer status = (Integer) entry.getValue().get(standPoint + "_status");
                    if (status == null || status == 0) {
                        isValid = false;
                        break;
                    }
                }
                // 过滤当前数据行，不满足条件的数据行不写入 CSV 文件
                if(!isValid)continue;
                StringBuilder line = new StringBuilder(entry.getKey().toString());
                for (String standPoint : realToStandLabel.values()) {
                    Double value = (Double) entry.getValue().get(standPoint);
                    line.append(",").append(value);
                    csvWriter.append(line.toString()).append("\n");
                }
            }
            return true;
        } catch (IOException e) {
            LOGGER.error("写入 CSV 文件失败", e);
            return false;
        }
    }
}
