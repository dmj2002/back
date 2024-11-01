package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Models implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer modelId;  // 模型id

    private String modelName;  // 模型名称

    private String modelLabel;  // 模型标签

    private List<String> turbineId;  // 风机id

    private Integer algorithmId;  // 算法id

    private String modelVersion;  // 模型版本

    private String modelParameters;  // 模型参数

    private Integer modelStatus;  // 模型状态

    private Integer creatorId;  // 创建者id

    private LocalDateTime createdTime;  // 创建时间

    private Integer lastActiviatedId;  // 最后使用id

    private LocalDateTime lastActiviatedTime;  // 最后使用时间

    private Integer patternId;  // 工况id

    private Integer moduleId;  // 模块id

    private Integer alertInterval;  // 每隔多少秒跑一次算法文件
}
