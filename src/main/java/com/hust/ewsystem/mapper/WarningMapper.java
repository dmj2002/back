package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.VO.WarningsVO;
import com.hust.ewsystem.entity.Warnings;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningMapper extends BaseMapper<Warnings> {

    List<WarningsVO> getWarningsByModelId(@Param("records") List<Warnings> records);
}
