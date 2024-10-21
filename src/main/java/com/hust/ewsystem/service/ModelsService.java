package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.entity.Models;

import java.util.Map;

public interface ModelsService extends IService<Models> {
    Map<String, Object> trainModel(Models model);
}
