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
public class WindFarm implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer windFarmId; // 风场id

    private String windFarmName; // 风场名

    private String windFarmAddress; // 风场地址

    private String contactNumber; // 风场电话

    private Integer companyId; // 公司id
}
