package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DTO.QueryTurbineWarnMatrixDTO;
import com.hust.ewsystem.DTO.QueryWarnDTO;
import com.hust.ewsystem.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.DTO.QueryWarnInfoDTO;
import com.hust.ewsystem.DTO.TrendDataDTO;
import com.hust.ewsystem.DTO.TurbineWarnMatrixDTO;
import com.hust.ewsystem.DTO.WarnCountDTO;
import com.hust.ewsystem.DTO.WarnHandleDTO;
import com.hust.ewsystem.DTO.WarningOperateDTO;
import com.hust.ewsystem.DTO.WarningsDTO;
import com.hust.ewsystem.VO.WarningsVO;
import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Company;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.entity.ReportWarningRelate;
import com.hust.ewsystem.entity.Reports;
import com.hust.ewsystem.entity.StandRealRelate;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.entity.WindFarm;
import com.hust.ewsystem.entity.WindTurbine;
import com.hust.ewsystem.mapper.CompanyMapper;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.mapper.ReportWarningRelateMapper;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.mapper.WarningMapper;
import com.hust.ewsystem.mapper.WindFarmMapper;
import com.hust.ewsystem.mapper.WindTurbineMapper;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.RealPortService;
import com.hust.ewsystem.service.ReportWarningRelateService;
import com.hust.ewsystem.service.StandRealRelateService;
import com.hust.ewsystem.service.WarningService;
import com.hust.ewsystem.service.WindFarmService;
import com.hust.ewsystem.service.WindTurbineService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/warning")
public class WarningController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarningController.class);

    @Autowired
    private WarningService warningService;

    @Autowired
    private ReportsMapper reportsMapper;

    @Autowired
    private WindFarmMapper windFarmMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private ModelsMapper modelsMapper;

    @Autowired
    private WindTurbineMapper windTurbineMapper;

    @Autowired
    private ModelsService modelsService;

    @Autowired
    private RealPortService realPortService;

    @Resource
    private StandRealRelateService standRealRelateService;

    @Autowired
    private WarningMapper warningMapper;

    @Autowired
    private ReportWarningRelateMapper reportWarningRelateMapper;

    @Resource
    private WindFarmService windFarmService;

    @Resource
    private WindTurbineService windTurbineService;

    @Resource
    private ReportWarningRelateService reportWarningRelateService;

    @GetMapping("/list")
    public EwsResult<?> getWarningList(@RequestParam(value = "page") int page,
                                       @RequestParam(value = "page_size") int pageSize,
                                       @RequestParam(value = "start_date") String startDate,
                                       @RequestParam(value = "end_date", required = false) String endDate,
                                       @RequestParam(value = "warning_level", required = false) Integer warningLevel,
                                       @RequestParam(value = "company_id", required = false) Integer companyId,
                                       @RequestParam(value = "windfarm_id", required = false) Integer windfarmId,
                                       @RequestParam(value = "module_id", required = false) Integer moduleId,
                                       @RequestParam(value = "turbine_id", required = false) Integer turbineId) {
        //先找modelId
        QueryWrapper<Models> queryWrapper = new QueryWrapper<>();
        if (moduleId != null) {
            queryWrapper.eq("module_id", moduleId);
        }
        if (turbineId != null) {
            queryWrapper.eq("turbine_id", turbineId);
        }
        else{
            List<Integer> turbineList = new ArrayList<>();
            if (companyId != null) {
                if (windfarmId != null) {
                    turbineList = windTurbineMapper.selectList(
                            new QueryWrapper<WindTurbine>().eq("wind_farm_id", windfarmId)
                    ).stream().map(WindTurbine::getTurbineId).collect(Collectors.toList());
                }
                else {
                    List<Integer> windfarmList = windFarmMapper.selectList(
                            new QueryWrapper<WindFarm>().eq("company_id", companyId)
                    ).stream().map(WindFarm::getWindFarmId).collect(Collectors.toList());
                    turbineList = windTurbineMapper.selectList(
                            new QueryWrapper<WindTurbine>().in("wind_farm_id", windfarmList)
                    ).stream().map(WindTurbine::getTurbineId).collect(Collectors.toList());
                }
            }
            if (!turbineList.isEmpty()) {
                queryWrapper.in("turbine_id", turbineList);
            }
        }
        List<Integer> modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());

        Page<Warnings> warningsPage = new Page<>(page, pageSize);
        QueryWrapper<Warnings> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.in("model_id", modelIdlist);
        if(endDate != null){
            queryWrapper2.ge("start_time", startDate).le("end_time", endDate);
        }
        else{
            queryWrapper2.ge("start_time", startDate).le("end_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if(warningLevel != null){
            queryWrapper2.eq("warning_level", warningLevel);
        }
        Page<Warnings> page1 = warningService.page(warningsPage, queryWrapper2);
//        if (page1.getRecords().isEmpty()) {
//            throw new CrudException("查询结果为空");
//        }
        Map<String,Object> result = new HashMap<>();
        if(!page1.getRecords().isEmpty()){
            List<WarningsVO> WarningsListVO = warningMapper.getWarningsByModelId(page1.getRecords());
            result.put("warningList",WarningsListVO);
        }else {
            result.put("warningList",new ArrayList<>());
        }
        result.put("total_count",page1.getTotal());
        result.put("page",page1.getCurrent());
        result.put("page_size",page1.getSize());
        result.put("total_pages",page1.getPages());
        return EwsResult.OK("查询成功", result);
    }
    
    @GetMapping("/nowList")
    public EwsResult<?> getNowWarningList(@RequestParam(value = "page") int page,
                                       @RequestParam(value = "page_size") int pageSize,
                                       @RequestParam(value = "warning_level", required = false) Integer warningLevel,
                                       @RequestParam(value = "company_id", required = false) Integer companyId,
                                       @RequestParam(value = "windfarm_id", required = false) Integer windfarmId,
                                       @RequestParam(value = "module_id", required = false) Integer moduleId,
                                       @RequestParam(value = "turbine_id", required = false) Integer turbineId) {
        //先找modelId
        QueryWrapper<Models> queryWrapper = new QueryWrapper<>();
        if (moduleId != null) {
            queryWrapper.eq("module_id", moduleId);
        }
        if (turbineId != null) {
            queryWrapper.eq("turbine_id", turbineId);
        }
        else{
            List<Integer> turbineList = new ArrayList<>();
            if (companyId != null) {
                if (windfarmId != null) {
                    turbineList = windTurbineMapper.selectList(
                            new QueryWrapper<WindTurbine>().eq("wind_farm_id", windfarmId)
                    ).stream().map(WindTurbine::getTurbineId).collect(Collectors.toList());
                }
                else {
                    List<Integer> windfarmList = windFarmMapper.selectList(
                            new QueryWrapper<WindFarm>().eq("company_id", companyId)
                    ).stream().map(WindFarm::getWindFarmId).collect(Collectors.toList());
                    turbineList = windTurbineMapper.selectList(
                            new QueryWrapper<WindTurbine>().in("wind_farm_id", windfarmList)
                    ).stream().map(WindTurbine::getTurbineId).collect(Collectors.toList());
                }
            }
            if (!turbineList.isEmpty()) {
                queryWrapper.in("turbine_id", turbineList);
            }
        }
        List<Integer> modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());

        Page<Warnings> warningsPage = new Page<>(page, pageSize);
        QueryWrapper<Warnings> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.in("model_id", modelIdlist);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String endDate = LocalDateTime.now().format(formatter);
        String startDate = LocalDateTime.now().minusHours(1).format(formatter);
        queryWrapper2.ge("end_time", startDate).le("end_time", endDate);
        if(warningLevel != null){
            queryWrapper2.eq("warning_level", warningLevel);
        }
        Page<Warnings> page1 = warningService.page(warningsPage, queryWrapper2);
//        if (page1.getRecords().isEmpty()) {
//            throw new CrudException("查询结果为空");
//        }
        Map<String,Object> result = new HashMap<>();
        if(!page1.getRecords().isEmpty()){
            List<WarningsVO> WarningsListVO = warningMapper.getWarningsByModelId(page1.getRecords());
            result.put("warningList",WarningsListVO);
        }else {
            result.put("warningList",new ArrayList<>());
        }
        result.put("total_count",page1.getTotal());
        result.put("page",page1.getCurrent());
        result.put("page_size",page1.getSize());
        result.put("total_pages",page1.getPages());
        return EwsResult.OK("查询成功", result);
    }
    /**
     * 查询预警详情趋势数据
     * @param queryWarnDetailsDTO queryWarnDetailsDTO
     * @return EwsResult<List<TrendDataDTO>>
     */
    @RequestMapping(value = "/trendData",method = RequestMethod.POST)
    public EwsResult<List<TrendDataDTO>> queryWarnDetail(@Valid @RequestBody QueryWarnDetailsDTO queryWarnDetailsDTO){
        List<Integer> standPointIdList = queryWarnDetailsDTO.getPointIdList();
        QueryWrapper<StandRealRelate> queryWrapper;
        QueryWrapper<RealPoint> realPointQueryWrapper;
        List<Map<Integer, String>> relPointAndLableList = new ArrayList<>();
        Map<Integer,String> relPointAndLableMap;
        // 根据风机ID和标准测点ID获取真实测点ID与测点标签的对应关系
        for (Integer standPointId : standPointIdList) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(StandRealRelate::getStandPointId,standPointId);
            List<StandRealRelate> standRealRelateList = standRealRelateService.list(queryWrapper);
            if (CollectionUtils.isEmpty(standRealRelateList)){
                LOGGER.error(String.format("标准测点id【%s】与对应的真实测点关联关系不存在",standPointId));
                return EwsResult.error("测点不存在,请检查参数后重试",null);
            }
            List<Integer> realPointList = new ArrayList<>();
            for (StandRealRelate standRealRelate : standRealRelateList) {
                realPointList.add(standRealRelate.getRealPointId());
            }
            realPointQueryWrapper = new QueryWrapper<>();
            realPointQueryWrapper.lambda().in(RealPoint::getPointId,realPointList).eq(RealPoint::getTurbineId,queryWarnDetailsDTO.getTurbineId());
            RealPoint realPoint = realPortService.getOne(realPointQueryWrapper);
            if (Objects.isNull(realPoint)){
                String realIdList = StringUtils.join(realPointList, ",");
                LOGGER.error(String.format("获取真实测点信息为空,真实测点id【%s】,风机id【%d】",realIdList,queryWarnDetailsDTO.getTurbineId()));
                return EwsResult.error("测点不存在,请检查参数后重试",null);
            }
            relPointAndLableMap = new HashMap<>();
            relPointAndLableMap.put(realPoint.getPointId(),realPoint.getPointLabel());
            relPointAndLableList.add(relPointAndLableMap);
        }

        // 查询测点值
        List<TrendDataDTO> realPointValueList = realPortService.getRealPointValueList(relPointAndLableList, queryWarnDetailsDTO);
        return EwsResult.OK(realPointValueList);
    }

    @PostMapping("/operate")
    public EwsResult<?> operateWarning(@RequestBody WarningOperateDTO warningOperateDTO) {
        //关闭待确认操作
        if(warningOperateDTO.getOperateCode() == 0){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setWarningStatus(3);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warning.setRepetition(warningOperateDTO.getRepetition());
                warning.setValid(warningOperateDTO.getValid());
                warningService.updateById(warning);
            }
            return EwsResult.OK("关闭成功");
        }
        //挂起操作
        else if(warningOperateDTO.getOperateCode() == 1){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setWarningStatus(1);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warningService.updateById(warning);
            }
            return EwsResult.OK("挂起成功");
        }
        //分级操作
        else if(warningOperateDTO.getOperateCode() == 2){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setHandleTime(LocalDateTime.now());
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setWarningLevel(warningOperateDTO.getWarningLevel());
                warningService.updateById(warning);
            }
            return EwsResult.OK("分级成功");
        }
        //通知操作
        else if(warningOperateDTO.getOperateCode() == 3){
            List<Integer> turbineIdList = warningMapper.getTurbineIdCountByWarningIdList(warningOperateDTO.getWarningId());
            if(turbineIdList.size() > 1){
                throw new CrudException("风机不唯一");
            }
            Reports report = Reports.builder()
                    .reportText(warningOperateDTO.getReportText())
                    .turbineId(turbineIdList.get(0))
                    .status(0)
                    .initialTime(LocalDateTime.now())
                    .valid(warningOperateDTO.getValid())
                    .repetition(warningOperateDTO.getRepetition())
                    .employeeId(warningOperateDTO.getOperatorId())
                    .build();
            reportsMapper.insert(report);
            for(Integer warningId : warningOperateDTO.getWarningId()){
                reportWarningRelateMapper.insert(ReportWarningRelate.builder()
                        .reportId(report.getReportId())
                        .warningId(warningId)
                        .build());
                Warnings warning = warningService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setWarningStatus(2);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warningService.updateById(warning);
            }
            return EwsResult.OK("通知成功",report);
        }
        //确认关闭操作
        else if(warningOperateDTO.getOperatorId() == 4){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningService.getById(warningId);
                if(warning == null || warning.getWarningStatus() != 3){
                    throw new CrudException("预警不存在或未关闭");
                }
                warning.setWarningStatus(4);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warningService.updateById(warning);
            }
            return EwsResult.OK("确认关闭成功");
        }
//        //通知办结操作
//        else if(warningOperateDTO.getOperatorId() == 5){
//            for(Integer warningId : warningOperateDTO.getWarningId()){
//                //判断预警是否处于通知状态并修改
//                Warnings warning = warningService.getById(warningId);
//                if(warning == null || warning.getWarningStatus() != 2){
//                    throw new CrudException("预警不存在或不处于通知状态");
//                }
//                warning.setWarningStatus(4);
//                warning.setHandlerId(warningOperateDTO.getOperatorId());
//                warning.setHandleTime(LocalDateTime.now());
//                warningService.updateById(warning);
//                // 修改通知状态为完结
//                ReportWarningRelate warningRelate = reportWarningRelateMapper.selectOne(new QueryWrapper<ReportWarningRelate>().eq("warning_id", warningId));
//                Reports report = reportsMapper.selectById(warningRelate.getReportId());
//                report.setStatus(2);
//                reportsMapper.updateById(report);
//            }
//            return EwsResult.OK("通知办结成功");
//        }
        else{
            throw new CrudException("操作码错误");
        }

    }


    /**
     * 概览页面-风机预警矩阵
     * @param queryTurbineWarnMatrixDTO queryWarnDetailsDTO
     * @return EwsResult<List<TurbineWarnMatrixDTO>>
     */
    @RequestMapping(value = "/queryTurbineWarnMatrix",method = RequestMethod.POST)
    public EwsResult<List<TurbineWarnMatrixDTO>> queryTurbineWarnMatrix(@Valid @RequestBody QueryTurbineWarnMatrixDTO queryTurbineWarnMatrixDTO){
        List<WindFarm> list = getWindFarmList(queryTurbineWarnMatrixDTO.getWindFarmId());
        List<TurbineWarnMatrixDTO> result = new ArrayList<>();
        WarnCountDTO warnCountDTO;
        TurbineWarnMatrixDTO turbineWarnMatrixDTO;
        for (WindFarm windFarm : list) {
            turbineWarnMatrixDTO = new TurbineWarnMatrixDTO();
            turbineWarnMatrixDTO.setWindFarmName(windFarm.getWindFarmName());
            Integer windFarmId = windFarm.getWindFarmId();
            LambdaQueryWrapper<WindTurbine> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WindTurbine::getWindFarmId,windFarmId);
            List<WindTurbine> windTurbines = windTurbineService.list(queryWrapper);
            List<WarnCountDTO> warnCounts = new ArrayList<>();
            for (WindTurbine windTurbine : windTurbines) {
                warnCountDTO = new WarnCountDTO();
                warnCountDTO.setTurbineId(windTurbine.getTurbineId());
                String turbineNumber = getTurbineNumber(windTurbine.getTurbineName());
                warnCountDTO.setTurbineNumber(Integer.parseInt(turbineNumber));
                int warnCount = getWarnCount(windTurbine.getTurbineId(), queryTurbineWarnMatrixDTO);
                warnCountDTO.setWarnCount(warnCount);
                warnCounts.add(warnCountDTO);
            }
            turbineWarnMatrixDTO.setWarnCountList(warnCounts);
            result.add(turbineWarnMatrixDTO);
        }
        return EwsResult.OK(result);
    }

    /**
     * 查询风场列表
     * @param windFarmId windFarmId
     * @return List<WindFarm>
     */
    public List<WindFarm> getWindFarmList(Integer windFarmId){
        List<WindFarm> list;
        if (CommonConstant.ALL.equals(windFarmId)){
            list = windFarmService.list();
        }else {
            LambdaQueryWrapper<WindFarm> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WindFarm::getWindFarmId,windFarmId);
            list = windFarmService.list(wrapper);
        }
        return list;
    }


    /**
     * 从风机名称总获取风机编号
     * @param turbineName 风机名称
     * @return String 风机编号
     */
    public String getTurbineNumber(String turbineName){
        // 使用正则表达式匹配数字
        java.util.regex.Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(turbineName);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            result.append(matcher.group());
        }
        return result.toString();
    }

    /**
     * 根据风机ID关联模型ID 获取预警数量
     * @param turbineId 风机id
     * @return String 风机预警总数量
     */
    public int getWarnCount(Integer turbineId,QueryTurbineWarnMatrixDTO queryTurbineWarnMatrixDTO){
        int warnCount = 0;
        LambdaQueryWrapper<Models> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Models::getTurbineId, turbineId);
        List<Models> list = modelsService.list(queryWrapper);
        for (Models models : list) {
            Integer modelId = models.getModelId();
            LambdaQueryWrapper<Warnings> warningsWrapper = new LambdaQueryWrapper<>();
            warningsWrapper.eq(Warnings::getModelId,modelId)
                    .ge(Warnings::getStartTime,queryTurbineWarnMatrixDTO.getStartDate()).le(Warnings::getEndTime,queryTurbineWarnMatrixDTO.getEndDate());
            List<Warnings> warnings = warningService.list(warningsWrapper);
            if (!CollectionUtils.isEmpty(warnings)){
                warnCount += warnings.size();
            }
        }
        return warnCount;
    }

    /**
     * 概览-预警列表
     * @param queryWarnDTO queryWarnDTO
     * @return EwsResult<IPage<Warnings>>
     */
    @RequestMapping(value = "/getWarnList",method = RequestMethod.POST)
    public EwsResult<IPage<WarningsDTO>> getWarnList(@Valid @RequestBody QueryWarnDTO queryWarnDTO){
        IPage<WarningsDTO> warnInfo = warningService.getWarnInfo(queryWarnDTO);
        return EwsResult.OK(warnInfo);
    }

    /**
     * 待处理预警-预警列表
     * @param queryWarnInfoDTO queryWarnInfoDTO
     * @return EwsResult<IPage<WarningsDTO>>
     */
    @RequestMapping(value = "/getWarnInfoList",method = RequestMethod.POST)
    public EwsResult<List<WarningsDTO>> getWarnInfoList(@Valid @RequestBody QueryWarnInfoDTO queryWarnInfoDTO){
        List<WarningsDTO> warnInfo = warningService.getWarnInfo(queryWarnInfoDTO);
        return EwsResult.OK(warnInfo);
    }

    /**
     * 预警处理（修改预警等级,关闭 挂起预警）
     * @param warnHandleDTO warnHandleDTO
     * @return EwsResult<Boolean>
     */
    @RequestMapping(value = "/warnHandle",method = RequestMethod.POST)
    public EwsResult<Boolean> warnHandle(@Valid @RequestBody WarnHandleDTO warnHandleDTO){
        Boolean result = warningService.warnHandle(warnHandleDTO);
        return EwsResult.OK("处理成功",result);
    }

    /**
     * 通知列表-根据通知ID查询预警列表
     * @param reportId reportId
     * @return EwsResult<Boolean>
     */
    @RequestMapping(value = "/getWarnInfoListByReportId",method = RequestMethod.GET)
    public EwsResult<List<Warnings>> getWarnInfoListByReportId(@RequestParam(value = "reportId") @NotNull(message = "通知ID不能为空") Integer reportId){
        LambdaQueryWrapper<ReportWarningRelate> relateWrapper = new LambdaQueryWrapper<>();
        relateWrapper.eq(ReportWarningRelate::getReportId,reportId);
        List<Integer> warnIdList = new ArrayList<>();
        List<ReportWarningRelate> reportWarningRelateList = reportWarningRelateService.list(relateWrapper);
        if (!CollectionUtils.isEmpty(reportWarningRelateList)){
            for (ReportWarningRelate reportWarningRelate : reportWarningRelateList) {
                warnIdList.add(reportWarningRelate.getWarningId());
            }
        }
        List<Warnings> warnInfoListByReportId = warningService.getWarnInfoListByReportId(warnIdList);
        return EwsResult.OK("处理成功",warnInfoListByReportId);
    }
}
