package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.DTO.QueryTurbineInfoDTO;
import com.hust.ewsystem.DTO.TurbineDetailsInfoDTO;
import com.hust.ewsystem.DTO.TurbineInfoDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Module;
import com.hust.ewsystem.entity.StandPoint;
import com.hust.ewsystem.entity.WindTurbine;
import com.hust.ewsystem.service.ModuleService;
import com.hust.ewsystem.service.StandPointService;
import com.hust.ewsystem.service.WindTurbineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.controller
 * @Author: xdy
 * @CreateTime: 2024-11-26  10:36
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/turbine")
public class TurbineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TurbineController.class);

    @Resource
    private ModuleService moduleService;

    @Resource
    private StandPointService standPointService;

    @Resource
    private WindTurbineService windTurbineService;


    /**
     * 查询风机信息
     * @param  tubineId
     * @return EwsResult<TurbineInfoDTO>
     */
    @RequestMapping(value = "/getTurbineInfo",method = RequestMethod.GET)
    public EwsResult<TurbineInfoDTO> getTurbineInfo(@RequestParam(value = "turbineId", required = true) Integer tubineId){
        QueryWrapper<Module> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Module::getTurbineId,tubineId);
        List<Module> list = moduleService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            LOGGER.error(String.format("获取风机模块信息为空,风机id【%s】",tubineId));
            return EwsResult.error(String.format("获取风机信息为空,风机id【%s】",tubineId));
        }
        TurbineInfoDTO turbineInfoDTO = initResult(tubineId, list);
        return EwsResult.OK(turbineInfoDTO);
    }

    @GetMapping("/list")
    public EwsResult<List<WindTurbine>> turbineList(@RequestParam(value = "windfarm_id", required = false) Integer windfarmId) {
        QueryWrapper<WindTurbine> windTurbineQueryWrapper = new QueryWrapper<>();
        if (windfarmId != null) {
            windTurbineQueryWrapper.eq("wind_farm_id", windfarmId);
        }
        List<WindTurbine> result = windTurbineService.list(windTurbineQueryWrapper);
        return EwsResult.OK(result);
    }

    /**
     * 组装风机信息结果
     * @param turbineId 风机id
     * @param moduleList 风机信息结果
     * @return TurbineInfoDTO
     */
    public TurbineInfoDTO initResult(Integer turbineId,List<Module> moduleList){
        TurbineInfoDTO result = new TurbineInfoDTO();
        result.setTurbineId(turbineId);
        List<TurbineDetailsInfoDTO> detailsInfoList =  new ArrayList<>(moduleList.size());
        TurbineDetailsInfoDTO turbineDetailsInfoDTO;
        QueryWrapper<StandPoint> queryWrapper;
        for (Module module : moduleList) {
            turbineDetailsInfoDTO = new TurbineDetailsInfoDTO();
            turbineDetailsInfoDTO.setModuleId(module.getModuleId());
            turbineDetailsInfoDTO.setModuleName(module.getModuleName());
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(StandPoint::getModuleId,module.getModuleId());
            List<StandPoint> list = standPointService.list(queryWrapper);
            turbineDetailsInfoDTO.setPointList(list);
            detailsInfoList.add(turbineDetailsInfoDTO);
        }
        result.setTurbineDetailsInfoList(detailsInfoList);
        return result;
    }


    /**
     * 查询风机列表
     * @param windFarmId windFarmId
     * @return EwsResult<TurbineInfoDTO>
     */
    @RequestMapping(value = "/getTurbineList",method = RequestMethod.GET)
    public EwsResult<List<WindTurbine>> getTurbineList(@NotNull(message = "风场ID不能为空") @RequestParam(value = "windFarmId") Integer windFarmId){
        LambdaQueryWrapper<WindTurbine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WindTurbine::getWindFarmId,windFarmId);
        List<WindTurbine> windTurbines = windTurbineService.list(queryWrapper);
        return EwsResult.OK(windTurbines);
    }
}
