package com.hust.ewsystem.DTO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelChangeDTO {

    private List<Integer> modelIds;

    private String modelName;

    private Integer alertWindowSize;

    private Integer alertInterval;
}
