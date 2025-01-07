package com.hust.ewsystem.controller;

import com.hust.ewsystem.DTO.QueryWaitDoneInfoDTO;
import com.hust.ewsystem.DTO.WindFarmDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.WindFarmService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.controller
 * @Author: xdy
 * @CreateTime: 2025-01-03  17:00
 * @Description:
 * @Version: 1.0
 */

@RestController
@RequestMapping("/waitdone")
public class WaitDoneController {

    @Resource
    private WindFarmService windFarmService;


    /**
     * 待办-查询待办信息
     * @param queryWaitDoneInfoDTO queryWaitDoneInfoDTO
     * @return EwsResult<List<WindFarmDTO>>
     */
    @RequestMapping(value = "/getWaitDoneInfo",method = RequestMethod.POST)
    public EwsResult<List<WindFarmDTO>> getWarnList(@Valid @RequestBody QueryWaitDoneInfoDTO queryWaitDoneInfoDTO){
        List<WindFarmDTO> windFarmsByCompanyId = windFarmService.getWindFarmsByCompanyId(queryWaitDoneInfoDTO);
        return EwsResult.OK(windFarmsByCompanyId);
    }
}
