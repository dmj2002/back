package com.hust.ewsystem.controller;

import ch.qos.logback.classic.gaffer.PropertyUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.DTO.TrendDataDTO;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.entity.StandRealRelate;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.entity.WindFarm;
import com.hust.ewsystem.entity.WindTurbine;
import com.hust.ewsystem.mapper.WindFarmMapper;
import com.hust.ewsystem.mapper.WindTurbineMapper;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.RealPortService;
import com.hust.ewsystem.service.StandRealRelateService;
import com.hust.ewsystem.service.WarningService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private WindFarmMapper windFarmMapper;

    @Autowired
    private WindTurbineMapper windTurbineMapper;

    @Autowired
    private ModelsService modelsService;

    @Autowired
    private RealPortService realPortService;

    @Resource
    private StandRealRelateService standRealRelateService;

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
        Map<String,Object> result = new HashMap<>();
        result.put("total_count",page1.getTotal());
        result.put("page",page1.getCurrent());
        result.put("page_size",page1.getSize());
        result.put("total_pages",page1.getPages());
        result.put("warningList",page1.getRecords());
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
}
