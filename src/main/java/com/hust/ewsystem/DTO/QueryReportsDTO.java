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
 * @CreateTime: 2025-01-08  11:47
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryReportsDTO implements Serializable {
    private static final long serialVersionUID = 4934623373871679934L;

    /**
     * 分页大小
     */
    @NotNull(message = "分页大小不能为空")
    private Integer pageSize;

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    private Integer pageNo;

    /**
     * 风机ID
     */
//    @NotNull(message = "风机ID不能为空")
    private Integer turbineId;

    /**
     * 风场ID
     */
    private Integer windFarmId;

    /**
     * 公司ID
     */
    private Integer companyId;

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
    private LocalDateTime endTime;
}
