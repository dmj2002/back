package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-08  09:31
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarnHandleDTO implements Serializable {
    private static final long serialVersionUID = 3656762393841599925L;

    /**
     * 预警等级
     */
    private Integer warnLevel;

    /**
     * 预警状态
     */
    private Integer warnStatus;

    /**
     * 预警id列表
     */
    @NotEmpty(message = "预警列表不能为空")
    private List<Integer> warnIdList;
}
