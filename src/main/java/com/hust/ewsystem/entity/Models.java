package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Models implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer modelId;  // 模型id

    private String modelName;  // 模型名称

    private Integer algorithmId;  // 算法id

    private String modelVersion;  // 模型版本

    private String modelOutputType;  // 模型输出类型

    private String modelOutputPath;

    private Double modelOutputThreshold;  // 模型输出阈值

    private String modelParameters;  // 模型参数

    private BigDecimal price;  // 价格

    private Integer modelStatus;  // 模型状态

    private Integer creatorId;  // 创建者id

    private LocalDateTime createTime;  // 创建时间

    private Integer patternId;  // 工况id
}
