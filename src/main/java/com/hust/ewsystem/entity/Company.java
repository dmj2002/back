package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Company extends Model <Company> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "公司id不能为空")
    @TableId
    private String id; // 公司id

    private String name; // 公司名

    private String pid; // 父公司id

    private String remarks; // 备注

    private Integer level; // 公司级别

    private Integer province; // 所在省份

}
