package com.hust.ewsystem.computmgt.service.impl;

import com.hust.ewsystem.common.util.CallComputUtil;
import com.hust.ewsystem.computmgt.entity.WkFileForm;
import com.hust.ewsystem.computmgt.service.AlgorithmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    @Resource
    private CallComputUtil callComputUtil;

    @Override
    public String testAlgorithm(WkFileForm wkFileForm) {
        try {
            callComputUtil.executeShellCmd(wkFileForm);
        } catch (Exception e) {
            // 异常根据实际需求处理
        }
        return "666";
    }
}
