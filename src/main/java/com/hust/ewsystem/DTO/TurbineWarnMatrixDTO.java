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
 * @CreateTime: 2025-01-03  10:56
 * @Description:
 * @Version: 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TurbineWarnMatrixDTO implements Serializable {

    private static final long serialVersionUID = 6765448415478258420L;

    /**
     * 风场名称
     */
    private String windFarmName;

    /**
     * 风机预警矩阵信息
     */
    private List<WarnCountDTO> warnCountList;
}
