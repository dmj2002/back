package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.VO.StandPointVO;
import com.hust.ewsystem.entity.AlgorithmStandRelate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author piiaJet
 * @Create 2024/12/1816:26
 */
public interface AlgorithmStandRelateMapper extends BaseMapper<AlgorithmStandRelate> {

    List<StandPointVO> getStandPointByAlgorithmId(@Param("algorithmId") Integer algorithmId);
}
