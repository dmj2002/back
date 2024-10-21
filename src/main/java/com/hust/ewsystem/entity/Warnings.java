package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Warnings implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private Integer warningId; // 预警id

    private Integer warningLevel; // 预警等级

    private Integer warningStatus; // 预警状态

    private LocalDateTime startTime; // 预警开始时间

    private LocalDateTime endTime; // 预警结束时间

    private Integer handlerId; // 处理人id

    private String warningDescription; // 预警描述

    private Integer transferredToWorkOrder; // 是否转入工单
}
