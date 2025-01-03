package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DTO.QueryWarnDTO;
import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.mapper.WarningMapper;
import com.hust.ewsystem.service.WarningService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WarningServiceImpl extends ServiceImpl<WarningMapper, Warnings> implements WarningService {

    @Resource
    private ModelsMapper modelsMapper;

    @Resource
    private WarningMapper warningMapper;


    /**
     * 根据风机ID关联模型ID 获取分页预警信息
     * @param queryWarnDTO queryWarnDTO
     * @return String 风机预警信息
     */
    @Override
    public IPage<Warnings> getWarnInfo(QueryWarnDTO queryWarnDTO) {
        Page<Warnings> page = new Page<>(queryWarnDTO.getPageNo(),queryWarnDTO.getPageSize());
        if (CommonConstant.ALL.equals(queryWarnDTO.getWindFarmId())){
            queryWarnDTO.setWindFarmId(null);
        }
        return warningMapper.selectWarningsPage(queryWarnDTO,page);
    }
}
