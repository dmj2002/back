package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.DTO.OverviewDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.WindTurbine;
import com.hust.ewsystem.mapper.WindTurbineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ShareController {

    @Autowired
    private WindTurbineMapper windTurbineMapper;

    @GetMapping("/overview")
    public EwsResult<?> overview(OverviewDTO overviewDTO) {
        //TODO 通过设备类型、公司类别、公司名称、时间段查询数据
        Map<String,Object> result = new HashMap<>();
        //设备品牌入口
        if(overviewDTO.getType()!=null){
            List<WindTurbine> turbineList = windTurbineMapper.selectList(new QueryWrapper<WindTurbine>().eq("turbine_type", overviewDTO.getType()));
            result.put("turbineList", turbineList);
        }
        //管理公司入口
        if(overviewDTO.getCategory()!=null && overviewDTO.getName()!=null){

        }
        return EwsResult.OK(result);
    }
}
