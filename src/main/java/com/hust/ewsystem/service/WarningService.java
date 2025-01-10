package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DTO.QueryWarnDTO;
import com.hust.ewsystem.DTO.QueryWarnInfoDTO;
import com.hust.ewsystem.DTO.WarnHandleDTO;
import com.hust.ewsystem.DTO.WarningsDTO;
import com.hust.ewsystem.entity.Warnings;

import java.util.List;

public interface WarningService extends IService<Warnings> {

    public IPage<WarningsDTO> getWarnInfo(QueryWarnDTO queryWarnDTO);

    public List<WarningsDTO> getWarnInfo(QueryWarnInfoDTO queryWarnDTO);

    public List<Warnings> getWarnInfoListByReportId(List<Integer> warnIdList);

    public Boolean warnHandle(WarnHandleDTO warnHandleDTO);
}
