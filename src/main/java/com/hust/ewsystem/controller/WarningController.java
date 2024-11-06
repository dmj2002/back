package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/warning")
public class WarningController {

    @Autowired
    private WarningService warningService;
    @GetMapping("/List")
    public EwsResult<?> getWarningList(@RequestParam(value = "page", required = true, defaultValue = "1") int page,
                                       @RequestParam(value = "page_size", required = true, defaultValue = "20") int pageSize,
                                       @RequestParam(value = "start_date", required = true) LocalDateTime startDate,
                                       @RequestParam(value = "end_date", required = false) LocalDateTime endDate,
                                       @RequestParam(value = "warning_level", required = false) int warningLevel,
                                       @RequestParam(value = "company_id", required = false) Integer companyId,
                                       @RequestParam(value = "windfarm_id", required = false) Integer windfarmId,
                                       @RequestParam(value = "module_id", required = false) Integer moduleId,
                                       @RequestParam(value = "turbine_id", required = false) Integer turbineId) {
        Page<Warnings> warningsPage = new Page<>(page, pageSize);
        QueryWrapper<Warnings> queryWrapper = new QueryWrapper<>();
        if (companyId != null) {
            queryWrapper.eq("company_id", companyId);
        }
        if (windfarmId != null) {
            queryWrapper.eq("windfarm_id", windfarmId);
        }
        if (moduleId != null) {
            queryWrapper.eq("module_id", moduleId);
        }
        if (turbineId != null) {
            queryWrapper.eq("turbine_id", turbineId);
        }
        Page<Warnings> page1 = warningService.page(warningsPage, queryWrapper);
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
}
