package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-03  17:12
 * @Description:
 * @Version: 1.0
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WindFarmDTO {

    /**
     * 风场的唯一标识符
     */
    private Integer windFarmId;

    /**
     * 风场的名称
     */
    private String windFarmName;

    /**
     * 公司Id
     */
    private Integer companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 风机待办信息
     */
    private List<TurbineWaitDoneInfo> turbineWaitDoneInfo;

}
