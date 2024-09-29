package com.hust.ewsystem.companyauth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.companyauth.entity.CompanyAuthModel;
import java.util.List;


public interface CompanyAuthModelService extends IService<CompanyAuthModel> {

    /**
     * 根据企业编号获取已授权模块列表
     * @param companyId 企业编号
     * @return CompanyMgr
     */
    public List<CompanyAuthModel> getCompAuthList(String companyId);

    /**
     * 新增企业与授权模块的关联关系
     * @param companyAuthModel 关联关系实体
     * @return Integer 影响行数
     */
    public boolean addCompAuth(CompanyAuthModel companyAuthModel);

    /**
     * 修改企业与授权模块的关联关系
     * @param companyAuthModel 关联关系实体
     * @return Integer 影响行数
     */
    public boolean updateCompAuth(CompanyAuthModel companyAuthModel);


    /**
     * 删除企业与授权模块的关联关系
     * @param companyId 企业编号
     * @return Integer 影响行数
     */
    public boolean delCompAuth(String companyId);
}
