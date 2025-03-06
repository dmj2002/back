package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-07  17:37
 * @Description:
 * @Version: 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryWarnInfoDTO implements Serializable {

    private static final long serialVersionUID = 3612242938736654559L;

    /**
     * 风场ID
     */
    @NotNull(message = "风场ID不能为空")
    private int windFarmId;

    /**
     * 风机ID
     */
    @NotNull(message = "风机ID不能为空")
    private int turbineId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime  endTime;

    /**
     * 预警描述
     */
    private String warningDescription;
}
