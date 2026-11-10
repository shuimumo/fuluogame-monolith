package com.shuimumu.fuluogame.service;

import com.shuimumu.fuluogame.dto.LoginDTO;
import java.util.Map;

public interface LoginService {
    String login(LoginDTO loginDTO);

    /** 邮箱验证码登录：给邮箱发送验证码 */
    void sendCode(String email);

    /** 邮箱密码登录 */
    Map<String, Object> loginByPassword(String email, String hashedPassword);

    void setPassword(Long userId, String hashedPassword);
}
