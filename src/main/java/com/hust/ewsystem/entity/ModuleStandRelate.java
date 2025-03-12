package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModuleStandRelate {

    @TableId(type = IdType.AUTO)
    private Integer id; // 记录id

    private Integer moduleId; // 模型id

    private Integer standPointId; // 真实测点id
}
