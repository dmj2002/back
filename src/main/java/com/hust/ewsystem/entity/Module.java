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
public class Module implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer moduleId; // 模块id

    private String moduleName; // 模块名称

    private Integer subsystemId; // 子系统id

    private Integer turbineId; // 风机id

    private String moduleDescription; // 描述



}
