package com.hust.ewsystem.controller;

import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Module;
import com.hust.ewsystem.entity.Subsystem;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.ModuleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.controller
 * @Author: xdy
 * @CreateTime: 2025-01-08  16:14
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/model")
public class ModuleController {

    @Resource
    private ModuleService moduleService;

    /**
     * 查询系统分类列表
     * @return EwsResult<TurbineInfoDTO>
     */
    @RequestMapping(value = "/getModelList",method = RequestMethod.GET)
    public EwsResult<List<Module>> getModelList(){
        List<Module> list = moduleService.list();
        return EwsResult.OK(list);
    }
}
