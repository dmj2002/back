package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Pattern implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer patternId; // 工况id

    private String patternName; // 工况名称

    private Integer patternStatus; // 工况状态

    private Integer patternPriority; // 工况优先级

    private Integer applicablePointType; // 适用测点类型

    private String operation; // 运算符

    private Integer logicOperator; // 逻辑运算符
}
