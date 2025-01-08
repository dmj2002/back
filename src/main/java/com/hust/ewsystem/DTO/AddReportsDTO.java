package com.hust.ewsystem.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hust.ewsystem.common.constant.CommonConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-08  10:46
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddReportsDTO implements Serializable {
    private static final long serialVersionUID = 7823974879600937010L;

    @NotBlank(message = "通知文本不能为空")
    private String reportText;  // 通知文本

    private String reportLabel;  // 报告标签

    @NotNull(message = "风机id不能为空")
    private Integer turbineId;  // 风机id

    @NotNull(message = "员工id不能为空")
    private Integer employeeId;  // 员工id

    @NotNull(message = "初始时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime initialTime;  // 初始时间

    @NotNull(message = "状态不能为空")
    private Integer status;  // 状态

    @NotNull(message = "是否重复不能为空")
    private Integer valid;//有效

    @NotNull(message = "是否重复不能为空")
    private Integer repetition;//重复

    @NotEmpty(message = "关联预警ID列表不能为空")
    private List<Integer> warnIdList;
}
