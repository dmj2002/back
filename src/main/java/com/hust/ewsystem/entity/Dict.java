package com.hust.ewsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Dict extends Model<Dict> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @NotNull(message = "字典id不能为空")
    private Integer id; //字典id

    @NotNull(message = "字典编号不能为空")
    private Integer num; //编号

    @NotNull(message = "父字典id不能为空")
    private Integer pid; //父字典id

    @NotBlank(message = "字典类型不能为空")
    private String name; //字典名称

    private String remarks; //备注

    private Integer rank; //等级

}
