package com.hust.ewsystem.usermanage.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User extends Model<User> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户id不能为空")
    @TableId
    private String id; // 用户id

    @NotBlank(message = "用户名不能为空")
    private String username; // 用户名

    @NotBlank(message = "密码不能为空")
    private String password; // 密码

    @NotBlank(message = "姓别不能为空")
    private Boolean sex; // 性别

    @Pattern(regexp = "^\\+86[0-9]{11}$", message = "电话号码无效")
    private String telephone; // 电话

    @Email(message = "邮件无效")
    private String email; // 邮箱

    private String cardId; //身份证

    @NotBlank(message = "Company ID cannot be blank")
    private String companyId; // 公司id

}
