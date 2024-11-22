package com.hust.ewsystem.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2024-11-22  09:54
 * @Description: 查询预警详情DTO
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryWarnDetailsDTO {

    /**
     * 真实测点ID
     */
    @NotNull(message = "模型ID不能为空")
    private Integer modelId;

    /**
     * 风机ID
     */
    @NotNull(message = "风机ID不能为空")
    private Integer turbineId;

    /**
     * 开始时间 yyyy-MM-dd HH:mm:ss
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    /**
     * 结束时间 yyyy-MM-dd HH:mm:ss
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
}
