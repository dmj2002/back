package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2024-11-26  10:12
 * @Description: 风机信息详情
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TurbineInfoDTO implements Serializable {

    private static final long serialVersionUID = 6018905405833679355L;

    /**
     * 所属风场ID
     */
    private Integer windFarmId;

    /**
     * 所属风场名称
     */
    private String windFarmName;

    /**
     * 风机ID
     */
    private Integer turbineId;

    /**
     * 风机名称
     */
    private String turbineName;

    /**
     * 模块列表
     */
    private List<TurbineDetailsInfoDTO> turbineDetailsInfoList;
}
