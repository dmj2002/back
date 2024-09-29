package com.hust.ewsystem.companyauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CompanyAuthModel extends Model<CompanyAuthModel> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("company_id")
    private String companyId;

    /**
     * 授权模块id,多个授权模块用“,”分隔 1屏幕水印、2网页水印、3文档水印、4图像水印、5视频水印
     */
    @TableField("auth_model_id")
    private String authModelIds;
}
