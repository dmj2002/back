package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Algorithm extends Model<Algorithm> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @NotNull(message = "算法id不能为空")
    private Integer algorithmId; // 算法id

    private String algorithmName; // 算法名

    private Integer algorithmType; // 算法类型

    private String description; // 算法描述

    private String rule; // 算法规则

    private String modelParameters; // 模型参数
}
