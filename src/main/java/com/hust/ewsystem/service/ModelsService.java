package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.VO.ThresholdVO;
import com.hust.ewsystem.entity.Models;

import java.io.File;
import java.util.List;


public interface ModelsService extends IService<Models> {
    String train(String algorithmLabel, String modelLabel,Integer modelId);

    //    Map<String, Object> getTaskStatus(String taskLabel);
    String killTask(String modelLabel);
    void predict(Integer alertInterval, String modelLabel, String algorithmLabel,Integer modelId,Integer alertWindowSize);

    void testPredict(Integer alertInterval, String modelLabel, String algorithmLabel, Integer modelId, Integer alertWindowSize, String startTime, String endTime);

    List<ThresholdVO> showThreshold(String modelLabel);

    void zipPredict(String modelLabel,String algorithmLabel, File resultDir,File[] csvFile,String label);

    void deleteTask(String label,String mode);
}
