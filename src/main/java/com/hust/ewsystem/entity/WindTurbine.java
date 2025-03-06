package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WindTurbine implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer turbineId; // 风机id

    private String turbineName; // 风机编号

    private String turbineType; // 风机型号

    private Double turbineCapacity; // 风机容量

    private Integer windFarmId; // 风场id

//    private Integer warningStatus; // 预警工况
//
//    private Integer currentStatus; // 当前工况
}
