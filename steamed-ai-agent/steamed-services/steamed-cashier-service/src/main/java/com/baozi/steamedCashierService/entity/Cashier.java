package com.baozi.steamedCashierService.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cashier")
@Tag(name = "收银员")
public class Cashier {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "收银员id")
    private Long id;

    @TableField("username")
    @Schema(description = "账号")
    private String username;

    @TableField("password")
    @Schema(description = "密码")
    private String password;

    @TableField("real_name")
    @Schema(description = "真实姓名")
    private String realName;

    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    @TableField(value = "status", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "状态")
    private Integer status;

    @TableField(value = "last_login_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @Schema(description = "是否删除")
    @TableLogic
    private Integer deleted;
}
