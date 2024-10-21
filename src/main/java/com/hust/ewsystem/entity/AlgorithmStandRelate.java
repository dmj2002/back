package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlgorithmStandRelate{

    @TableId
    private Integer id; // 记录id

    private Integer algorithmId; // 算法id

    private Integer standPointId; // 标准测点id
}
