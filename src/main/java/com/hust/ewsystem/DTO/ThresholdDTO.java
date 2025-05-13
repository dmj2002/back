package com.hust.ewsystem.DTO;

import com.hust.ewsystem.VO.ThresholdVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ThresholdDTO implements Serializable {

    private static final long serialVersionUID = 7823974879600937010L;

    private Integer modelId;

    private Object items;
}
