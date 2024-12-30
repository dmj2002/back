package com.hust.ewsystem.controller;


import com.hust.ewsystem.DTO.ReportDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.mapper.ReportsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {
    
    @Autowired
    private ReportsMapper reportsMapper;
    
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
}
