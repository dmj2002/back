package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-07  12:20
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarnStatusDTO {
    /**
     * 待处理数量 预警状态为0的数量
     */
    private int waitDoneSum;

    /**
     * 挂起数量 预警状态为1的数量
     */
    private int hangUp;

    /**
     * 处理中 预警状态为2的数量
     */
    private int processIng;

    /**
     * 关闭待确认数量 预警状态为3的数量
     */
    private int closeWaitDoneSum;
}
