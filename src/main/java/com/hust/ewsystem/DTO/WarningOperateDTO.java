package com.hust.ewsystem.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarningOperateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> warningId;

    private Integer operateCode;

    private Integer warningLevel;

    private String reportText;

    private Integer operatorId;
}
