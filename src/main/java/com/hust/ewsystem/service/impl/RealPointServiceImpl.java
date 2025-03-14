package com.hust.ewsystem.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.DTO.TrendDataDTO;
import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.common.util.DateUtil;
import com.hust.ewsystem.entity.CommonData;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.mapper.CommonDataMapper;
import com.hust.ewsystem.mapper.RealPointMapper;
import com.hust.ewsystem.service.RealPointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2024-11-22  11:03
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class RealPointServiceImpl extends ServiceImpl<RealPointMapper, RealPoint> implements RealPointService {

    @Resource
    private CommonDataMapper commonDataMapper;

    @Override
    @DS("slave")
    public List<TrendDataDTO> getRealPointValueList(List<Map<Integer, String>> pointLabels, QueryWarnDetailsDTO queryWarnDetailsDTO) {
        List<CommonData> valueList;
        List<TrendDataDTO> result = new LinkedList<>();
        String startDate = DateUtil.dateTimeToDateString(queryWarnDetailsDTO.getStartDate(), CommonConstant.DATETIME_FORMAT_1);
        String endDate = DateUtil.dateTimeToDateString(queryWarnDetailsDTO.getEndDate(), CommonConstant.DATETIME_FORMAT_1);
        TrendDataDTO trendDataDTO;
        for (Map<Integer, String> pointLabel : pointLabels) {
            for (Map.Entry<Integer, String> entry : pointLabel.entrySet()) {
                valueList = commonDataMapper.selectDataByTime(entry.getValue().toLowerCase(), startDate, endDate);
                trendDataDTO = new TrendDataDTO();
                trendDataDTO.setPointId(entry.getKey());
                trendDataDTO.setPointValue(valueList);
                result.add(trendDataDTO);
            }
        }
        return result;
    }
}
