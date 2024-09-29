package com.hust.ewsystem.companyauth.controller;


import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.companyauth.entity.CompanyAuthModel;
import com.hust.ewsystem.companyauth.service.CompanyAuthModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/compAuthMgr")
public class CompanyAuthModelController {

    @Resource
    private CompanyAuthModelService companyAuthModelService;

    /**
     * 新增企业授权关联关系
     * @param companyAuthModel 企业授权关联关系实体
     * @return
     */
    @RequestMapping(value = "/addCompAuth")
    public EwsResult<Boolean> addCompAuth(CompanyAuthModel companyAuthModel) {
        // TODO 以下为示例 根据实际业务处理 根据需要返回  异常和日志看情况处理
        boolean addCompAuth = companyAuthModelService.addCompAuth(companyAuthModel);
        return EwsResult.ok(addCompAuth);
    }

    /**
     * 查询企业授权关联关系
     * @param companyId 企业编号
     * @return
     */
    @RequestMapping(value = "/getCompAuthList")
    public EwsResult<List<CompanyAuthModel>> getCompAuthList(String companyId) {
        // TODO 以下为示例 根据实际业务处理 根据需要返回  异常和日志看情况处理
        List<CompanyAuthModel> compAuthList = companyAuthModelService.getCompAuthList(companyId);
        return EwsResult.ok(compAuthList);
    }

    /**
     * 更新企业授权关联关系
     * @param companyAuthModel 企业授权关联关系实体
     * @return 返回更新结果
     */
    @RequestMapping(value = "/updateCompAuth")
    public EwsResult<Boolean> updateCompAuth(CompanyAuthModel companyAuthModel) {
        // TODO 以下为示例 根据实际业务处理 根据需要返回  异常和日志看情况处理
        boolean compAuth = companyAuthModelService.updateCompAuth(companyAuthModel);
        return EwsResult.ok(compAuth);
    }

    /**
     * 删除企业授权关联关系
     * @param companyId 企业编号
     * @return 返回删除结果
     */
    @RequestMapping(value = "/delCompAuth")
    public EwsResult<Boolean> delCompAuth(String companyId) {
        // TODO 以下为示例 根据实际业务处理 根据需要返回  异常和日志看情况处理
        boolean delCompAuth = companyAuthModelService.delCompAuth(companyId);
        return EwsResult.ok(delCompAuth);
    }

    /**
     * 路径收参示例
     * 删除企业授权关联关系
     * @param companyName 企业名称
     * @return 返回删除结果
     */
    @RequestMapping(value = "/delCompAuth/{companyName}")
    public EwsResult<Boolean> delComp1(@PathVariable String companyName) {
        // TODO 以下为示例 根据实际业务处理 根据需要返回  异常和日志看情况处理
        boolean delCompAuth = companyAuthModelService.delCompAuth(companyName);
        return EwsResult.ok(delCompAuth);
    }

    /**
     * 单个参数接收示例
     * 删除企业授权关联关系
     * @param companyName 企业名称
     * @return 返回删除结果
     */
    @RequestMapping(value = "/delCompAuth2")
    public EwsResult<Boolean> delComp2(@RequestParam String companyName) {
        // TODO 以下为示例 根据实际业务处理 根据需要返回  异常和日志看情况处理
        boolean delCompAuth = companyAuthModelService.delCompAuth(companyName);
        return EwsResult.ok(delCompAuth);
    }

    /**
     * POST实体收参收参示例
     * 删除企业授权关联关系
     * @param companyAuthModel
     * @return 返回删除结果
     */
    @RequestMapping(value = "/delCompAuth3")
    public EwsResult<CompanyAuthModel> delComp3(@RequestBody CompanyAuthModel companyAuthModel) {
        // TODO 以下为示例 根据实际业务处理 根据需要返回  异常和日志看情况处理
        return EwsResult.ok(null);
    }


    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;


    @RequestMapping(value = "/getKey/{key}",method = RequestMethod.GET)
    public EwsResult<CompanyAuthModel> setKey(@PathVariable String key) throws IOException {

        // 操作redis 实际应用时 自己查一下
        Object o = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set("a","a");
        return EwsResult.ok(null);
    }
}
