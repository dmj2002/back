package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2024-11-26  10:41
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryTurbineInfoDTO implements Serializable {

    private static final long serialVersionUID = -3089087508290375077L;

//    /**
//     * 所属风场ID
//     */
//    @NotNull(message = "所属风场ID不能为空")
//    private Integer windFarmId;
//
//    /**
//     * 所属风场名称
//     */
//    @NotBlank(message = "所属风场名称不能为空")
//    private String windFarmName;

    /**
     * 风机ID
     */
    @NotNull(message = "风机ID不能为空")
    private Integer turbineId;

//    /**
//     * 风机名称
//     */
//    @NotBlank(message = "风机名称不能为空")
//    private String turbineName;
}
