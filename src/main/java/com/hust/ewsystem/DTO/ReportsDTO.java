package com.hust.ewsystem.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-08  12:13
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportsDTO {

    /**
     * 通知ID
     */
    private Integer reportId;

    /**
     * 通知文本，用于存储报告的具体内容描述。
     */
    private String reportText;

    /**
     * 报告标签，用于对报告进行分类或标记，方便管理和查询。
     */
    private String reportLabel;

    /**
     * 风机的唯一标识符，关联到相应的风机信息。
     */
    private Integer turbineId;

    /**
     * 员工的唯一标识符，关联到生成该报告的员工信息。
     */
    private Integer employeeId;

    /**
     * 员工查姓名
     */
    private String employeeName;

    /**
     * 初始时间，记录报告的创建或初始时间。
     */
    private LocalDateTime initialTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 有效标志，用于表示该报告是否有效。
     */
    private Integer valid;

    /**
     * 重复标志，用于表示该报告是否为重复报告。
     */
    private Integer repetition;

}
