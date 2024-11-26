package com.hust.ewsystem.DTO;

import com.hust.ewsystem.entity.RealPoint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2024-11-26  10:25
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TurbineDetailsInfoDTO implements Serializable {

    private static final long serialVersionUID = 1831923390306535732L;

    /**
     * 模块ID
     */
    private Integer moduleId;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 模型关联的测点列表
     */
    private List<RealPoint> pointList;
}
