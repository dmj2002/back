package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FarmDTO implements Serializable {

    private static final long serialVersionUID = -3089087508290375077L;

    /**
     * 所属风场ID
     */
    private Integer windFarmId;

    /**
     * 所属风场名称
     */
    private String windFarmName;

    /**
     * 风机ID
     */
    private Integer turbineId;

    /**
     * 风机名称
     */
    private String turbineName;
}
