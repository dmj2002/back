package com.hust.ewsystem.computmgt.controller;

import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.common.util.CallComputUtil;
import com.hust.ewsystem.computmgt.entity.WkFileForm;
import com.hust.ewsystem.computmgt.service.AlgorithmService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/algorithm")
public class AlgorithmController {


    @Resource
    private CallComputUtil callComputUtil;

    @Resource
    private AlgorithmService algorithmService;


    /**
     *算法调用示例
     * @param wkFileForm 算法参数
     * @return
     */
    @RequestMapping(value = "/testAlgorithm")
    public EwsResult<Boolean> testAlgorithm(WkFileForm wkFileForm) {
        // TODO 参数校验  业务参数补全  业务处理等 异常和日志看情况处理
        // 算法调用
        algorithmService.testAlgorithm(wkFileForm);
        // TODO 结果处理等
        return EwsResult.ok(null);
    }

}
