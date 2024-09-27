package com.hust.ewsystem.companyauth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.companyauth.entity.CompanyAuthModel;

/**
 * @BelongsProject: MarkDetectMgr
 * @BelongsPackage: com.hust.fastdev.mapper
 * @Author: xdy
 * @CreateTime: 2024-05-16  10:44
 * @Description:
 * @Version: 1.0
 */
public interface CompanyAuthModelMapper extends BaseMapper<CompanyAuthModel> {

    // 数据层的简单的增删改查直接调用mybatis-plus封装好的接口，对于mybatis-plus不能满足要求的自己定义接口 在mapper中写sql

}
