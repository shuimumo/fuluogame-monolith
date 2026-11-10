package com.shuimumu.fuluogame.service;

public interface MailService {
    /**
     * 发送登录验证码邮件。
     *
     * @param toEmail 收件邮箱
     * @param code    6 位数字验证码
     */
    void sendLoginCode(String toEmail, String code);
}
