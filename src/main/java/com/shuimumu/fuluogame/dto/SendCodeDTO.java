package com.shuimumu.fuluogame.dto;

import lombok.Data;

@Data
public class SendCodeDTO {
    /** 收件邮箱（登录验证码） */
    private String email;
}
