package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.WindFarm;
import com.hust.ewsystem.entity.WindTurbine;
import com.hust.ewsystem.service.WindFarmService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.controller
 * @Author: xdy
 * @CreateTime: 2025-01-03  10:21
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/windfarm")
public class WindFarmController {

    @Resource
    private WindFarmService windFarmService;

//    @RequestMapping(value = "/getWindFarmList",method = RequestMethod.GET)
//    public EwsResult<List<WindFarm>> getWindFarmList(){
//        List<WindFarm> list = windFarmService.list();
//        return EwsResult.OK(list);
//    }
    @GetMapping("/getwindfarmList")
    public EwsResult<?> getWindFarm(@RequestParam(value = "companyId",required = false) Integer companyId){
        QueryWrapper<WindFarm> queryWrapper = new QueryWrapper<>();
        if(companyId != null){
            queryWrapper.eq("company_id", companyId);
        }
        List<WindFarm> res = windFarmService.list(queryWrapper);
        return EwsResult.OK(res);
    }

    @GetMapping("/list")
    public EwsResult<List<WindFarm>> windfarmList(@RequestParam(value = "companyId",required = false) Integer companyId) {
        QueryWrapper<WindFarm> windFarmQueryWrapper = new QueryWrapper<>();
        if (companyId != null) {
            windFarmQueryWrapper.eq("company_id", companyId);
        }
        List<WindFarm> result = windFarmService.list(windFarmQueryWrapper);
        return EwsResult.OK(result);
    }
}
