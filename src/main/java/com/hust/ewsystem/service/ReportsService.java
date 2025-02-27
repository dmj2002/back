package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DTO.QueryReportsDTO;
import com.hust.ewsystem.DTO.AddReportsDTO;
import com.hust.ewsystem.DTO.ReportsDTO;
import com.hust.ewsystem.entity.Reports;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service
 * @Author: xdy
 * @CreateTime: 2025-01-08  10:31
 * @Description:
 * @Version: 1.0
 */
public interface ReportsService extends IService<Reports> {

    public int addReport(AddReportsDTO addReportsDTO);

    public IPage<ReportsDTO> getReportList(QueryReportsDTO queryReportsDTO);
}
