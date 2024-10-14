package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.entity.Models;

import java.util.Map;

public interface ModelService extends IService<Models> {
    Map<String, Object> trainModel(Models model);
}
