package com.hust.ewsystem.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModulePointVO {

    private List<Integer> pointIds;

    private Integer moduleId;
}
