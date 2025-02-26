package com.hust.ewsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hust.ewsystem.DTO.AddReportsDTO;
import com.hust.ewsystem.DTO.QueryReportsDTO;
import com.hust.ewsystem.DTO.ReportDTO;
import com.hust.ewsystem.DTO.ReportsDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.ReportWarningRelate;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.ReportWarningRelateService;
import com.hust.ewsystem.service.ReportsService;
import com.hust.ewsystem.service.WarningService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
public class ReportController {
    
    @Resource
    private ReportsMapper reportsMapper;

    @Resource
    private ReportsService reportsService;

    @Resource
    private ReportWarningRelateService reportWarningRelateService;

    @Resource
    private WarningService warningService;

    
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
    @GetMapping("/deleteReport")
    public EwsResult<?> deleteReport(@RequestParam("reportId") Integer reportId){
        List<Integer> warning_ids = reportWarningRelateService.list(new QueryWrapper<ReportWarningRelate>().eq("report_id", reportId)).stream().map(ReportWarningRelate::getWarningId).collect(Collectors.toList());
        boolean res = warningService.list(new QueryWrapper<Warnings>().in("warning_id", warning_ids)).stream().allMatch(
                warning -> warning.getWarningStatus() == 4 || warning.getWarningStatus() == 3
        );
        if(res){
            Reports report = reportsMapper.selectById(reportId);
            report.setStatus(2);
            reportsMapper.updateById(report);
            return EwsResult.OK("通知办结成功");
        }
        return EwsResult.error("通知办结失败");
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
