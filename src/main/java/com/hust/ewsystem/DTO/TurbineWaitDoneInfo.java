package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-03  17:15
 * @Description:
 * @Version: 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TurbineWaitDoneInfo {

    /**
     * 风机ID
     */
    private Integer turbineId;

    /**
     * 风机名称
     */
    private String turbineName;

    /**
     * 一级预警数
     */
    private Integer warningLevel1Sum;

    /**
     * 二级预警数
     */
    private Integer warningLevel2Sum;

    /**
     * 通知总数
     */
    private Integer reportSum;

    private String turbineType;
    private Integer windFarmId;
    private Integer warningStatus;
    private Integer currentStatus;


    /**
     * 模型中预警数量和通知数量
     */
    private List<ModelsDTO> modelList;
}
