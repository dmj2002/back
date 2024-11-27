package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2024-11-26  10:31
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RealPointBaseInfoDTO implements Serializable {

    private static final long serialVersionUID = -485948372404631672L;

    /**
     * 测点ID
     */
    private Integer pointId;

    /**
     * 测点描述
     */
    private String pointDescription;
}
