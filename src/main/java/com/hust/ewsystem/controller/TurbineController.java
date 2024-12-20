package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.DTO.QueryTurbineInfoDTO;
import com.hust.ewsystem.DTO.TurbineDetailsInfoDTO;
import com.hust.ewsystem.DTO.TurbineInfoDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Module;
import com.hust.ewsystem.entity.StandPoint;
import com.hust.ewsystem.service.ModuleService;
import com.hust.ewsystem.service.StandPointService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.Valid;
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

    @Resource
    private ModuleService moduleService;

    @Resource
    private StandPointService standPointService;


    /**
     * 查询风机信息
     * @param queryTurbineInfoDTO queryTurbineInfoDTO
     * @return EwsResult<TurbineInfoDTO>
     */
    @RequestMapping(value = "/getTurbineInfo",method = RequestMethod.POST)
    public EwsResult<TurbineInfoDTO> getTurbineInfo(@Valid @RequestBody QueryTurbineInfoDTO queryTurbineInfoDTO){
        QueryWrapper<Module> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Module::getTurbineId,queryTurbineInfoDTO.getTurbineId());
        List<Module> list = moduleService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return EwsResult.error(String.format("未查询到风机信息,风机id【%s】",queryTurbineInfoDTO.getTurbineId()));
        }
        TurbineInfoDTO turbineInfoDTO = initResult(queryTurbineInfoDTO, list);
        return EwsResult.OK(turbineInfoDTO);
    }

    /**
     * 组装风机信息结果
     * @param queryTurbineInfoDTO 风机信息结果
     * @param moduleList 风机信息结果
     * @return TurbineInfoDTO
     */
    public TurbineInfoDTO initResult(QueryTurbineInfoDTO queryTurbineInfoDTO,List<Module> moduleList){
        TurbineInfoDTO result = new TurbineInfoDTO();
        result.setTurbineId(queryTurbineInfoDTO.getTurbineId());
        result.setTurbineNumber(queryTurbineInfoDTO.getTurbineNumber());
        result.setWindFarmId(queryTurbineInfoDTO.getWindFarmId());
        result.setWindFarmName(queryTurbineInfoDTO.getWindFarmName());
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
}
