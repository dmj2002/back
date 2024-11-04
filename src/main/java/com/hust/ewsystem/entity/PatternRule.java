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
public class PatternRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer patternRuleId;  // 规则id

    private Integer patternId;  // 工况id

    private Integer pointId;  // 测点id

    private String operator;  // 操作符

    private Double value;  // 阈值

    private Integer logicSymbol;  // 逻辑符
}
