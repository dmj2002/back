package com.hust.ewsystem.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandPointVO {

    private Integer pointId; // 标准测点id

    private String pointLabel; // 标准测点名称
}
