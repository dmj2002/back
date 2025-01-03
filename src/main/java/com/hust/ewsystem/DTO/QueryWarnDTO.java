package com.hust.ewsystem.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hust.ewsystem.common.constant.CommonConstant;
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
 * @CreateTime: 2025-01-03  14:21
 * @Description:
 * @Version: 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryWarnDTO implements Serializable {
    private static final long serialVersionUID = 308655687426724124L;

    /**
     * 分页大小
     */
    private int pageSize;

    /**
     * 页码
     */
    private int pageNo;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime  endTime;

    /**
     * 分场id,"全部"用int 9999标识
     */
    @NotNull(message = "分场id不能为空")
    private Integer  windFarmId;
}
