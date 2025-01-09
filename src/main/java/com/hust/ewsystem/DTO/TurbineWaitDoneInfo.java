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
     * 风机的一级预警待处理数 预警状态为0 预警等级为1的数量
     */
    private int warningLevel1waitDoneSum;

    /**
     * 风机的二级预警待处理数 预警状态为0 预警等级为2的数量
     */
    private int warningLevel2waitDoneSum;

    /**
     * 风机的一级预警挂起数 预警状态为1的数量 预警等级为1的数量
     */
    private int warningLevel1waitHangUpSum;

    /**
     * 风机的二级预警挂起数 预警状态为1的数量 预警等级为2的数量
     */
    private int warningLevel2waitHangUpSum;

    /**
     * 风机的一级预警关闭待确认数 预警状态为3 预警等级为1的数量
     */
    private int warningLevel1waitCloseWaitSum;

    /**
     * 风机的二级预警关闭待确认数 预警状态为3 预警等级为2的数量
     */
    private int warningLevel2waitCloseWaitSum;

    /**
     * 通知总数
     */
    private int reportSum;

    /**
     * a设备类型
     */
    private String turbineType;

    /**
     * 风场ID
     */
    private Integer windFarmId;

    /**
     * 预警状态
     */
    private Integer warningStatus;

    /**
     * 当前状态
     */
    private Integer currentStatus;


    /**
     * 模型中预警数量和通知数量
     */
    private List<ModelsDTO> modelList;
}
