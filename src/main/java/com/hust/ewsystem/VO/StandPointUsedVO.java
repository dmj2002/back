package com.hust.ewsystem.VO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandPointUsedVO {

    private Integer pointId; // 测点id

    private String pointLabel; // 测点标签

    private String pointDescription; // 测点描述

    private Integer used; // 表示该测点是否被使用
}
