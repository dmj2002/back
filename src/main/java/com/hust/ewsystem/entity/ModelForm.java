package com.hust.ewsystem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelForm {

    private List<Integer> turbineList;  // 风机id

    private List<String> pointList;  // 标准测点标签

    private Models model;  // 模型
}
