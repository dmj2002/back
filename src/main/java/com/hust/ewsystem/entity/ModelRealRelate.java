package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelRealRelate {

    @TableId
    private Integer id; // 记录id

    private Integer modelId; // 模型id

    private Integer realPointId; // 真实测点id
}

