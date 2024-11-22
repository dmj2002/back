package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.entity.CommonData;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.entity.RealPointValue;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service
 * @Author: xdy
 * @CreateTime: 2024-11-22  11:03
 * @Description:
 * @Version: 1.0
 */
public interface RealPortService extends IService<RealPoint> {

    /**
     * 查询正真实测点数据
     * @param pointLabels
     * @param queryWarnDetailsDTO
     * @return
     */
    public Map<Integer,List<CommonData>> getRealPointValueList(List<Map<Integer, String>> pointLabels, QueryWarnDetailsDTO queryWarnDetailsDTO);
}
