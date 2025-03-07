package com.hust.ewsystem.service.impl;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DTO.QueryReportsDTO;
import com.hust.ewsystem.DTO.AddReportsDTO;
import com.hust.ewsystem.DTO.ReportsDTO;
import com.hust.ewsystem.entity.Employee;
import com.hust.ewsystem.entity.ReportWarningRelate;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.mapper.EmployeeMapper;
import com.hust.ewsystem.mapper.ReportWarningRelateMapper;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.ReportsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    private EmployeeMapper employeeMapper;

    @Resource
    private ReportWarningRelateMapper reportWarningRelateMapper;

    @Override
    public int addReport(AddReportsDTO reportDTO) {
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

    @Override
    public IPage<ReportsDTO> getReportList(QueryReportsDTO queryReportsDTO) {
        Page<Reports> page = new Page<>(queryReportsDTO.getPageNo(), queryReportsDTO.getPageSize());
        QueryWrapper<Reports> queryWrapper = new QueryWrapper<>();
        if(queryReportsDTO.getWindFarmId() != null){
            queryWrapper.eq("wind_farm_id",queryReportsDTO.getWindFarmId());
        }
        if(queryReportsDTO.getTurbineId() != null){
            queryWrapper.eq("turbine_id",queryReportsDTO.getTurbineId());
        }
        queryWrapper.ge("initial_time",queryReportsDTO.getStartTime()).le("initial_time",queryReportsDTO.getEndTime());
        Page<Reports> reportsPage = reportsMapper.selectPage(page, queryWrapper);
        LambdaQueryWrapper<Employee> wrapper;
        Page<ReportsDTO> reportsDTOPage = new Page<>();
        List<ReportsDTO> list = new ArrayList<>();
        for (Reports record : reportsPage.getRecords()) {
            Integer employeeId = record.getEmployeeId();
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Employee::getEmployeeId,employeeId);
            Employee employee = employeeMapper.selectOne(wrapper);
            ReportsDTO reportsDTO = initResult(record, employee);
            list.add(reportsDTO);
        }
        reportsDTOPage.setRecords(list);
        reportsDTOPage.setTotal(reportsPage.getTotal());
        reportsDTOPage.setSize(reportsPage.getSize());
        reportsDTOPage.setCurrent(reportsPage.getCurrent());
        reportsDTOPage.setOrders(reportsPage.orders());
        reportsDTOPage.setOptimizeCountSql(reportsPage.optimizeCountSql());
        reportsDTOPage.setSearchCount(reportsPage.searchCount());
        reportsDTOPage.setPages(reportsPage.getPages());
        return reportsDTOPage;
    }

    /**
     * 组装查询结果(通知列表中的处理人信息)
     * @param record record
     * @param employee employee
     * @return ReportsDTO
     */
    public ReportsDTO initResult(Reports record,Employee employee){
        ReportsDTO reportsDTO = new ReportsDTO();
        reportsDTO.setReportId(record.getReportId());
        reportsDTO.setReportText(record.getReportText());
        reportsDTO.setReportLabel(reportsDTO.getReportLabel());
        reportsDTO.setTurbineId(record.getTurbineId());
        reportsDTO.setEmployeeId(record.getEmployeeId());
        reportsDTO.setEmployeeName(employee.getEmployeeName());
        reportsDTO.setInitialTime(record.getInitialTime());
        reportsDTO.setStatus(record.getStatus());
        reportsDTO.setValid(record.getValid());
        reportsDTO.setRepetition(record.getRepetition());
        return reportsDTO;
    }
}
