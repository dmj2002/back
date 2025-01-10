package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DTO.QueryWaitDoneInfoDTO;
import com.hust.ewsystem.DTO.WindFarmDTO;
import com.hust.ewsystem.entity.WindFarm;

import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service
 * @Author: xdy
 * @CreateTime: 2025-01-03  10:22
 * @Description:
 * @Version: 1.0
 */
public interface WindFarmService extends IService<WindFarm> {
    /**
     * 查询风场风机信息
     * @param queryWaitDoneInfoDTO queryWaitDoneInfoDTO
     * @return List<WindFarmVO>
     */
    public List<WindFarmDTO> getWindFarmsByCompanyId(QueryWaitDoneInfoDTO queryWaitDoneInfoDTO);
}
