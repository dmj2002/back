package com.hust.ewsystem.companyauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.companyauth.entity.CompanyAuthModel;
import com.hust.ewsystem.companyauth.mapper.CompanyAuthModelMapper;
import com.hust.ewsystem.companyauth.service.CompanyAuthModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @BelongsProject: MarkDetectMgr
 * @BelongsPackage: com.hust.fastdev.service.impl
 * @Author: xdy
 * @CreateTime: 2024-05-16  10:46
 * @Description: 企业与授权模块关联关系Service实现
 * @Version: 1.0
 */
@Service
@Transactional
public class CompanyAuthModelServiceImpl extends ServiceImpl<CompanyAuthModelMapper, CompanyAuthModel> implements CompanyAuthModelService {


    @Override
    public List<CompanyAuthModel> getCompAuthList(String companyId) {
        QueryWrapper<CompanyAuthModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",companyId);
        CompanyAuthModel companyAuthModel = new CompanyAuthModel();
        // 数据层的简单的增删改查直接调用mybatis-plus封装好的接口，对于mybatis-plus不能满足要求的自己定义接口 在mapper中写sql
        return companyAuthModel.selectList(queryWrapper);
    }

    @Override
    public boolean addCompAuth(CompanyAuthModel companyAuthModel) {
        // 数据层的简单的增删改查直接调用mybatis-plus封装好的接口，对于mybatis-plus不能满足要求的自己定义接口 在mapper中写sql
        return companyAuthModel.insert();
    }

    @Override
    public boolean updateCompAuth(CompanyAuthModel companyAuthModel) {
        QueryWrapper<CompanyAuthModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",companyAuthModel.getCompanyId());
        // 数据层的简单的增删改查直接调用mybatis-plus封装好的接口，对于mybatis-plus不能满足要求的自己定义接口 在mapper中写sql
        return companyAuthModel.update(queryWrapper);
    }

    @Override
    public boolean delCompAuth(String companyId) {
        QueryWrapper<CompanyAuthModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",companyId);
        CompanyAuthModel companyAuthModel = new CompanyAuthModel();
        // 数据层的简单的增删改查直接调用mybatis-plus封装好的接口，对于mybatis-plus不能满足要求的自己定义接口 在mapper中写sql
        return companyAuthModel.delete(queryWrapper);
    }
}
