package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DTO.QueryWarnDTO;
import com.hust.ewsystem.entity.Warnings;

public interface WarningService extends IService<Warnings> {

    public IPage<Warnings> getWarnInfo(QueryWarnDTO queryWarnDTO);
}
