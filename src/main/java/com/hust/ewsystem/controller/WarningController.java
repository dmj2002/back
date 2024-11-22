package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.CommonData;
import com.hust.ewsystem.entity.ModelRealRelate;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.entity.WindFarm;
import com.hust.ewsystem.entity.WindTurbine;
import com.hust.ewsystem.mapper.WindFarmMapper;
import com.hust.ewsystem.mapper.WindTurbineMapper;
import com.hust.ewsystem.service.ModelRealRelateService;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.RealPortService;
import com.hust.ewsystem.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/warning")
public class WarningController {

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
    @Autowired
    private ModelRealRelateService modelRealRelateService;
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

    @RequestMapping(value = "/detail",method = RequestMethod.POST)
    public EwsResult<Object> queryWarnDetail(@Valid @RequestBody QueryWarnDetailsDTO queryWarnDetailsDTO){

        // 根据风机ID和测点列表获取测点标签
        Integer modelId = queryWarnDetailsDTO.getModelId();
        QueryWrapper<ModelRealRelate> modelRealRelateWrapper = new QueryWrapper<>();
        modelRealRelateWrapper.lambda().eq(ModelRealRelate::getModelId,modelId);
        List<ModelRealRelate> modelRealRelateList = modelRealRelateService.list(modelRealRelateWrapper);
        if (CollectionUtils.isEmpty(modelRealRelateList)){
            return EwsResult.OK("测点数据为空",null);
        }
        List<Integer> realPointIdList = modelRealRelateList.stream().map(ModelRealRelate::getRealPointId).collect(Collectors.toList());

        QueryWrapper<RealPoint> realPointQueryWrapper = new QueryWrapper<>();
        realPointQueryWrapper.lambda().eq(RealPoint::getTurbineId,queryWarnDetailsDTO.getTurbineId()).in(RealPoint::getPointId,realPointIdList);
        List<RealPoint> realPointList = realPortService.list(realPointQueryWrapper);

        List<Map<Integer, String>> list = realPointList.stream().map(point -> {
                    Map<Integer, String> map = new HashMap<>();
                    map.put(point.getPointId(), point.getPointLabel());
                    return map;
                }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(list)){
            return EwsResult.OK("测点数据为空",null);
        }

        // 查询测点值
        Map<Integer, List<CommonData>> realPointValueList = realPortService.getRealPointValueList(list, queryWarnDetailsDTO);
        return EwsResult.OK(realPointValueList);
    }
}
