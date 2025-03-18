package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.DTO.FarmDTO;
import com.hust.ewsystem.DTO.QueryWaitDoneInfoDTO;
import com.hust.ewsystem.DTO.WindFarmDTO;
import com.hust.ewsystem.entity.WindFarm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WindFarmMapper extends BaseMapper<WindFarm> {

     List<WindFarmDTO> getWindFarmsByCompanyId(@Param("param") QueryWaitDoneInfoDTO queryWaitDoneInfoDTO);

    FarmDTO getWindFarmByTurbineId(@Param("turbineId")Integer turbineId);
}
