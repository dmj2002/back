package com.hust.ewsystem.controller;


import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/model")
public class ModelsController {

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/training")
    public EwsResult<?> trainModel(@RequestBody Models model) {
        Map<String, Object> result = modelsService.trainModel(model);
        return (result != null && !result.isEmpty())
                ? EwsResult.OK("训练成功", result)
                : EwsResult.error("训练失败");

    }
}
