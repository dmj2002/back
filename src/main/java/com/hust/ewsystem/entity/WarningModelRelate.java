package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarningModelRelate {

    @TableId
    private Integer id; // 记录id

    private Integer warningId; // 预警id

    private Integer modelId; // 模型id
}
