package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RealPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer pointId; // 测点id

    private String pointLabel; // 测点标签

    private String pointDescription; // 测点描述

    private Integer moduleId; // 模块id

    private String pointUnit; // 测点单位

    private Integer pointType; // 测点类型

    private Integer turbineId; // 风机id

    private String calculate;
}
