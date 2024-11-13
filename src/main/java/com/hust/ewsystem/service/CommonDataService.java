package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.entity.CommonData;

import java.util.List;

public interface CommonDataService extends IService<CommonData> {
    List<CommonData> selectAllData(String tableName);

    List<CommonData> selectDataByTime(String tableName, String startTime, String endTime);
}
