package com.hust.ewsystem.controller;

import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Subsystem;
import com.hust.ewsystem.service.SubsystemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.controller
 * @Author: xdy
 * @CreateTime: 2025-01-08  14:46
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/system")
public class SubSystemController {

    @Resource
    private SubsystemService subsystemService;

    /**
     * 查询系统分类列表
     * @return EwsResult<TurbineInfoDTO>
     */
    @RequestMapping(value = "/getSubSystemList",method = RequestMethod.GET)
    public EwsResult<List<Subsystem>> getTurbineList(){
        List<Subsystem> windTurbines = subsystemService.list();
        return EwsResult.OK(windTurbines);
    }
}
