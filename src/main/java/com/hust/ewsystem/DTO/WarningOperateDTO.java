package com.hust.ewsystem.DTO;

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

    private List<Integer> WarningId;

    private Integer OperateCode;

    private Integer WarningLevel;

    private String ReportText;

    private Integer operateId;
}
