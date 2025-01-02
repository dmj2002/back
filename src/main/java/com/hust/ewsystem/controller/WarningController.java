package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.DTO.TrendDataDTO;
import com.hust.ewsystem.DTO.WarningOperateDTO;
import com.hust.ewsystem.VO.WarningsVO;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.*;
import com.hust.ewsystem.mapper.*;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.RealPortService;
import com.hust.ewsystem.service.StandRealRelateService;
import com.hust.ewsystem.service.WarningService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        if (page1.getRecords().isEmpty()) {
            throw new CrudException("查询结果为空");
        }
        QueryWrapper<WindTurbine> windTurbineQueryWrapper = new QueryWrapper<>();
        windTurbineQueryWrapper.select("turbine_id","turbine_type", "turbine_name","wind_farm_id");  // 指定你需要的字段
        List<WindTurbine> turbineList = windTurbineMapper.selectList(windTurbineQueryWrapper);


        QueryWrapper<WindFarm> windFarmQueryWrapper = new QueryWrapper<>();
        windFarmQueryWrapper.select("wind_farm_id", "wind_farm_name,company_id");
        List<WindFarm> windFarmList = windFarmMapper.selectList(windFarmQueryWrapper);

        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.select("company_id", "company_name");
        List<Company> companyList = companyMapper.selectList(companyQueryWrapper);

        QueryWrapper<Models> modelsQueryWrapper = new QueryWrapper<>();
        modelsQueryWrapper.select("model_id","turbine_id");
        List<Models> modelsList = modelsMapper.selectList(modelsQueryWrapper);

        //TODO: 什么意思？？？把所有数据都查出来吗？ or 根据模型Id查询(可能联表写的sql可能有点小bug)？
        List<WarningsVO> WarningsListVO = warningMapper.getWarningsByModelId(page1.getRecords());
        Map<String,Object> result = new HashMap<>();
        result.put("total_count",page1.getTotal());
        result.put("page",page1.getCurrent());
        result.put("page_size",page1.getSize());
        result.put("total_pages",page1.getPages());
        result.put("warningList",WarningsListVO);
        result.put("companyList",companyList);
        result.put("windFarmList",windFarmList);
        result.put("turbineList",turbineList);
        result.put("modelList",modelsList);
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
        //关闭操作
        if(warningOperateDTO.getOperateCode() == 0){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setWarningStatus(3);
                warning.setHandlerId(warningOperateDTO.getOperateId());
                warning.setHandleTime(LocalDateTime.now());
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
                warning.setWarningStatus(3);
                warning.setHandlerId(warningOperateDTO.getOperateId());
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
                warning.setWarningStatus(2);
                warning.setHandleTime(LocalDateTime.now());
                warning.setWarningLevel(warningOperateDTO.getWarningLevel());
                warningService.updateById(warning);
            }
            return EwsResult.OK("分级成功");
        }
        //通知操作
        else if(warningOperateDTO.getOperateCode() == 3){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Integer turbineId = warningMapper.getTurbineIdByWarningId(warningId);
                Reports report = Reports.builder()
                        .reportText(warningOperateDTO.getReportText())
                        .turbineId(turbineId)
                        .status(0)
                        .initialTime(LocalDateTime.now())
                        .build();
                reportsMapper.insert(report);
                reportWarningRelateMapper.insert(ReportWarningRelate.builder()
                                        .reportId(report.getReportId())
                                        .warningId(warningId)
                                        .build());
            }
            return EwsResult.OK("通知成功");
        }
        else{
            throw new CrudException("操作码错误");
        }

    }
}
