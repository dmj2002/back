package com.hust.ewsystem.controller;


import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/model")
public class ModelsController {

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/train")
    public EwsResult<?> train(@RequestBody Map<String, Object> FileForm) {
        // TODO 参数校验  业务参数补全  业务处理等 异常和日志看情况处理
        // 算法调用
        String result = modelsService.train(FileForm);
        // TODO 结果处理等
        return EwsResult.OK(result);
    }

}
