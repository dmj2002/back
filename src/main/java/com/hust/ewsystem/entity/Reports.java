package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Reports implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type=IdType.AUTO)
    private Integer reportId;  // 报告id

    private String reportLabel;  // 报告标签

    private Integer turbineIdl;  // 风机id

    private Integer employeeId;  // 员工id

    private LocalDateTime initialTime;  // 初始时间

    private Integer status;  // 状态

    private String reportText; //通知文本
}
