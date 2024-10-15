package com.hust.ewsystem.controller;


import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.Company;
import com.hust.ewsystem.service.CompanyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Resource
    private CompanyService companyService;

    @PostMapping("/add")
    public EwsResult<Boolean> addCompany(@RequestBody @Validated Company company) {
        boolean result = companyService.save(company);
        return result ? EwsResult.ok("添加成功") : EwsResult.error("添加失败");
    }

    @GetMapping("/get/{id}")
    public EwsResult<?> getCompany(@PathVariable String id) {
        Company company = companyService.getById(id);
        return Objects.isNull(company) ? EwsResult.error("未找到公司") : EwsResult.ok(company);
    }

    @PutMapping("/update")
    public EwsResult<Boolean> updateCompany(@RequestBody @Validated Company company) {
        boolean result = companyService.updateById(company);
        return result ? EwsResult.ok("更新成功") : EwsResult.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public EwsResult<Boolean> deleteCompany(@PathVariable String id) {
        boolean result = companyService.removeById(id);
        return result ? EwsResult.ok("删除成功") : EwsResult.error("删除失败");
    }

    @GetMapping("/list")
    public EwsResult<List<Company>> listCompany() {
        List<Company> company = companyService.list();
        return EwsResult.ok(company);
    }
}
