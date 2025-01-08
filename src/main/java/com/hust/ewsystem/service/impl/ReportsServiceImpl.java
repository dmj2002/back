package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DTO.ReportDTO;
import com.hust.ewsystem.DTO.ReportsDTO;
import com.hust.ewsystem.entity.ReportWarningRelate;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.mapper.ReportWarningRelateMapper;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2025-01-08  10:32
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class ReportsServiceImpl extends ServiceImpl<ReportsMapper, Reports> implements ReportsService {

    @Resource
    private ReportsMapper reportsMapper;

    @Resource
    private ReportWarningRelateMapper reportWarningRelateMapper;

    @Override
    public int addReport(ReportsDTO reportDTO) {
        Reports repeat = Reports.builder().reportLabel(reportDTO.getReportLabel()).turbineId(reportDTO.getTurbineId()).employeeId(reportDTO.getEmployeeId())
                .initialTime(reportDTO.getInitialTime()).status(reportDTO.getStatus()).reportText(reportDTO.getReportText())
                .valid(reportDTO.getValid()).repetition(reportDTO.getRepetition()).build();
        reportsMapper.insert(repeat);
        Integer reportId = repeat.getReportId();
        List<Integer> warnIdList = reportDTO.getWarnIdList();
        for (Integer warnId : warnIdList) {
            ReportWarningRelate reportWarningRelate = ReportWarningRelate.builder().reportId(reportId).warningId(warnId).build();
            reportWarningRelateMapper.insert(reportWarningRelate);
        }
        return 0;
    }
}
