package com.hust.ewsystem.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarningsVO {

    private Integer warningId; // 预警id

    private Integer warningLevel; // 预警等级

    private Integer warningStatus; // 预警状态

    private Integer modelId; // 模型id

    private LocalDateTime startTime; // 预警开始时间

    private LocalDateTime endTime; // 预警结束时间

    private Integer handlerId; // 处理人id

    private LocalDateTime handleTime; // 处理时间

    private String warningDescription; // 预警描述

    private Integer transferredToWorkOrder; // 是否转入工单

    private Integer taskId; // 任务id

    private String turbineName; // 风机名称
}
