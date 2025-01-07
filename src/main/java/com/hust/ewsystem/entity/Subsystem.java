package com.hust.ewsystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.entity
 * @Author: xdy
 * @CreateTime: 2025-01-07  17:56
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Subsystem implements Serializable {

    private static final long serialVersionUID = 4111736302466531958L;

    private int subsystemId;
    private String subsystemName;
    private int turbineId;
    private String description;
}
