package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.entity.Models;

import java.util.Map;

public interface ModelsService extends IService<Models> {
    String train(String algorithmLabel);
    Map<String, Object> getTaskStatus(String taskId);
    String killTask(String taskId);
}
