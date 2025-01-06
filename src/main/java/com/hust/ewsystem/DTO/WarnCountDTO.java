package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-03  10:47
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarnCountDTO implements Serializable {

    private static final long serialVersionUID = -4996443561817656507L;

    /**
     * 风机id(风机表主键标识)
     */
    private Integer turbineId;

    /**
     * 风机编号
     */
    private Integer turbineNumber;

    /**
     * 预警数量
     */
    private Integer warnCount;
}
