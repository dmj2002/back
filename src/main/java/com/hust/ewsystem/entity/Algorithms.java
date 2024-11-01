package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Algorithms implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer algorithmId; // 算法id
    @NotBlank(message = "算法名称不能为空")
    private String algorithmLabel; // 算法编号

    private String algorithmVersion; // 算法版本

    private String algorithmFilePath; // 算法文件路径

    private Integer outputType; // 输出类型

    private String description; // 算法描述
}
