package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.entity.CommonData;
import org.apache.ibatis.annotations.Param;
import com.baomidou.dynamic.datasource.annotation.DS;

import java.time.LocalDateTime;
import java.util.List;

public interface CommonDataMapper extends BaseMapper<CommonData> {

    List<CommonData> selectAllData(@Param("tableName")String tableName);

    List<CommonData> selectDataByTime(@Param("tableName")String tableName, @Param("startTime") String startTime, @Param("endTime")String endTime);
}
