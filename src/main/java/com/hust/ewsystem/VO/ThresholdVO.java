package com.hust.ewsystem.VO;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ThresholdVO {

    @JSONField(name = "下限")
    private Double lowerLimit;

    @JSONField(name = "上限")
    private Double upperLimit;

    @JSONField(name = "范围")
    private List<Double> range;
}
