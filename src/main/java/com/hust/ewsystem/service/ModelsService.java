package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.entity.Models;

import java.util.Map;

public interface ModelsService extends IService<Models> {
    String train(String algorithmLabel, String modelLabel);
    Map<String, Object> getTaskStatus(String taskId);
    String killTask(String taskId);
    String predict(Integer alertInterval, String modelLabel, String algorithmLabel);
}
