package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.mapper.RealPointMapper;
import com.hust.ewsystem.service.RealPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/realpoint")
public class RealPointController {

    @Autowired
    private RealPointMapper realPointMapper;

    @Autowired
    private RealPointService realPointService;



    @PostMapping("/add")
    public EwsResult<?> addRealPoint(@RequestBody RealPoint realPoint) {
        int inserted = realPointMapper.insert(realPoint);
        if(inserted != 0){
            Map<String,Object> result = new HashMap<>();
            result.put("pointId", realPoint.getPointId());
            result.put("pointLabel", realPoint.getPointLabel());
            result.put("pointDescription", realPoint.getPointDescription());
            result.put("module_id", realPoint.getModuleId());
            result.put("pointType", realPoint.getPointType());
            result.put("pointUnit", realPoint.getPointUnit());
            result.put("turbine_id", realPoint.getTurbineId());
            return EwsResult.OK("测点添加成功", result);
        }
        else{
            return EwsResult.error("测点添加失败");
        }
    }
    @GetMapping("/get/{pointId}")
    public EwsResult<?> getRealPoint(@PathVariable Integer pointId){
        RealPoint realPoint = realPointMapper.selectById(pointId);
        return Objects.isNull(realPoint) ? EwsResult.error("未找到该测点") : EwsResult.OK("测点查找成功",realPoint);
    }
    @GetMapping("/list")
    public EwsResult<?> listRealPoint(@RequestParam(value = "page") int page,
                                      @RequestParam(value = "page_size") int pageSize){
        Map<String,Object> response = new HashMap<>();
        Page<RealPoint> modelsPage = new Page<>(page, pageSize);
        QueryWrapper<RealPoint> queryWrapper = new QueryWrapper<>();

        QueryWrapper<RealPoint> realPointQueryWrapper = new QueryWrapper<>();
        realPointQueryWrapper.select("point_id", "point_label","point_description","turbine_id");
        List<RealPoint> windFarmList = realPointMapper.selectList(realPointQueryWrapper);

        Page<RealPoint> page1 = realPointService.page(modelsPage, queryWrapper);



        response.put("total_count",page1.getTotal());
        response.put("page",page1.getCurrent());
        response.put("page_size",page1.getSize());
        response.put("total_pages",page1.getPages());
        response.put("realPointList",page1.getRecords());
        return EwsResult.OK("查询成功", response);
    }
}
