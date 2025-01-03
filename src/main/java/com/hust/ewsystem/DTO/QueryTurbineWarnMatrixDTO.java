package com.hust.ewsystem.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hust.ewsystem.common.constant.CommonConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-03  10:36
 * @Description: 查询风机预警矩阵DTO
 * @Version: 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryTurbineWarnMatrixDTO implements Serializable {

    private static final long serialVersionUID = -1139754386615996311L;

    /**
     * 设备型号名,当 type 存在时，是设备品牌入口
     */
    private String type;

    /**
     * 公司类型，有权属公司、管理公司和区域几个类别；分别为1，2，3
     */
    private String category;

    /**
     * 公司名称
     */
    private String name;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime startDate;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime  endDate;

    /**
     * 分场id,"全部"用int 9999标识
     */
    @NotNull(message = "分场id不能为空")
    private Integer  windFarmId;
}
