package com.hust.ewsystem.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.common.util.DateUtil;
import com.hust.ewsystem.entity.CommonData;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.mapper.CommonDataMapper;
import com.hust.ewsystem.mapper.RealPortMapper;
import com.hust.ewsystem.service.RealPortService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
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
public class RealPortServiceImpl extends ServiceImpl<RealPortMapper, RealPoint> implements RealPortService {

    @Resource
    private CommonDataMapper commonDataMapper;

    @Override
    @DS("slave")
    public Map<Integer,List<CommonData>> getRealPointValueList(List<Map<Integer, String>> pointLabels, QueryWarnDetailsDTO queryWarnDetailsDTO) {
        List<CommonData> valueList;
        Map<Integer,List<CommonData>> result = new LinkedHashMap<>();
        String startDate = DateUtil.dateTimeToDateString(queryWarnDetailsDTO.getStartDate(), CommonConstant.DATETIME_FORMAT_1);
        String endDate = DateUtil.dateTimeToDateString(queryWarnDetailsDTO.getEndDate(), CommonConstant.DATETIME_FORMAT_1);
        for (Map<Integer, String> pointLabel : pointLabels) {
            for (Map.Entry<Integer, String> entry : pointLabel.entrySet()) {
                valueList = commonDataMapper.selectDataByTime(entry.getValue().toLowerCase(), startDate, endDate);
                result.put(entry.getKey(),valueList);
            }
        }
        return result;
    }
}
