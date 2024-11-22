package com.hust.ewsystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2024-11-22  10:01
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RealPointValue {

    /**
     * 时间
     */
    private Date dateTime;

    /**
     * 状态
     */
    private int status;

    /**
     * 测点值
     */
    private double value;
}
