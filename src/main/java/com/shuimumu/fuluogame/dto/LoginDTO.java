package com.shuimumu.fuluogame.dto;

import lombok.Data;

@Data
public class LoginDTO{
    /** 登录账号：验证码登录与密码登录统一使用邮箱 */
    private String email;
    private String password;
    private String code;
}
