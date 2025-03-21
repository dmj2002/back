package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-03  17:19
 * @Description:
 * @Version: 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelsDTO {

    /**
     * 模型id
     */
    private Integer modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 一级预警数
     */
    private int warningLevel1Sum;

    /**
     * 二级预警数
     */
    private int warningLevel2Sum;
}
