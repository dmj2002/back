package com.hust.ewsystem.DTO;

import com.hust.ewsystem.mapper.StandRealRelateMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OverviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;  // 设备类型

    private Integer category;  // 公司类别

    private String name;  // 公司名称

    private LocalDateTime startTime;  // 开始时间

    private LocalDateTime endTime;  // 结束时间
}
