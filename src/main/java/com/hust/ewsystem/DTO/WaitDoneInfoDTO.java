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
 * @CreateTime: 2025-01-03  17:06
 * @Description:
 * @Version: 1.0
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WaitDoneInfoDTO implements Serializable {

    private static final long serialVersionUID = -6458220321683268856L;

    /**
     * 风机风场统计信息
     */
    private List<WindFarmDTO> windFarmList;
}
