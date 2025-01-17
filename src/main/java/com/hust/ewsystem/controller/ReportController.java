package com.hust.ewsystem.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hust.ewsystem.DTO.AddReportsDTO;
import com.hust.ewsystem.DTO.QueryReportsDTO;
import com.hust.ewsystem.DTO.ReportDTO;
import com.hust.ewsystem.DTO.ReportsDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.ReportsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/report")
public class ReportController {
    
    @Resource
    private ReportsMapper reportsMapper;

    @Resource
    private ReportsService reportsService;

    
    @PostMapping("/operate")
    public EwsResult<?> operationReport(@RequestBody ReportDTO reportDTO){
        Reports newReport = Reports.builder()
                .reportId(reportDTO.getReportId())
                .status(reportDTO.getReportStatus())
                .build();
        // 仅在 reportText 不为 null 时，才设置该字段
        if (reportDTO.getReportText() != null) {
            newReport.setReportText(reportDTO.getReportText());
        }
        int res = reportsMapper.updateById(newReport);
        return res == 1 ? EwsResult.OK("操作成功") : EwsResult.error("操作失败");
    }

    /**
     * 新增待处理通知
     * @param reportsDTO reportsDTO
     * @return EwsResult<Boolean>
     */
    @RequestMapping(value = "/addReport",method = RequestMethod.POST)
    public EwsResult<Boolean> addReport(@Valid @RequestBody AddReportsDTO reportsDTO){
        int i = reportsService.addReport(reportsDTO);
        return i == 0 ? EwsResult.OK("操作成功",true) : EwsResult.error("操作成功",false);
    }

    /**
     * 查询通知列表
     * @param queryReportsDTO queryReportsDTO
     * @return EwsResult<IPage<Reports>>
     */
    @RequestMapping(value = "/getReportList",method = RequestMethod.POST)
    public EwsResult<IPage<ReportsDTO>>  getReportList(@Valid @RequestBody QueryReportsDTO queryReportsDTO){
        IPage<ReportsDTO> reportList = reportsService.getReportList(queryReportsDTO);
        return EwsResult.OK(reportList);
    }
}
