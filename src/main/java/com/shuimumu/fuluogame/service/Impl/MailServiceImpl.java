package com.shuimumu.fuluogame.service.Impl;

import cn.hutool.core.util.DesensitizedUtil;
import com.shuimumu.fuluogame.constants.SystemRedisKeys;
import com.shuimumu.fuluogame.exception.BusinessException;
import com.shuimumu.fuluogame.service.MailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${fuluo.mail.enabled:true}")
    private boolean enabled;

    @Value("${fuluo.mail.from:}")
    private String from;

    @Value("${fuluo.mail.from-name:福洛游戏}")
    private String fromName;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Override
    public void sendLoginCode(String toEmail, String code) {
        // 本地调试开关：不真正发信，验证码直接打日志，避免测试环境反复发邮件
        if (!enabled) {
            log.warn("邮件发送已关闭（fuluo.mail.enabled=false），登录验证码 to={} code={}",
                    DesensitizedUtil.email(toEmail), code);
            return;
        }
        if (mailSender == null) {
            log.error("JavaMailSender 未初始化，请检查 spring.mail 配置");
            throw new BusinessException("验证码邮件发送失败，请稍后重试");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            String sender = (from == null || from.isBlank()) ? mailUsername : from;
            helper.setFrom(new InternetAddress(sender, fromName, StandardCharsets.UTF_8.name()));
            helper.setTo(toEmail);
            helper.setSubject("【福洛游戏】登录验证码");
            helper.setText(buildPlainText(code), buildHtml(code));
            mailSender.send(message);
            log.info("登录验证码邮件已发送 to={}", DesensitizedUtil.email(toEmail));
        } catch (Exception e) {
            log.error("登录验证码邮件发送失败 to={}", DesensitizedUtil.email(toEmail), e);
            throw new BusinessException("验证码邮件发送失败，请稍后重试");
        }
    }

    private String buildPlainText(String code) {
        return "弗洛游戏登录验证码：" + code + "，" + SystemRedisKeys.LOGIN_CODE_TTL
                + " 分钟内有效。请勿泄露给他人。";
    }

    private String buildHtml(String code) {
        return "<div style=\"font-family:Arial,Helvetica,sans-serif;max-width:480px;margin:0 auto;"
                + "padding:24px;color:#2D2D2D;\">"
                + "<h2 style=\"margin:0 0 12px;color:#ef6f8e;\">弗洛游戏</h2>"
                + "<p style=\"margin:0 0 16px;\">你正在登录弗洛游戏，本次登录验证码为：</p>"
                + "<p style=\"margin:0 0 16px;font-size:30px;font-weight:bold;letter-spacing:8px;color:#ef6f8e;\">"
                + code + "</p>"
                + "<p style=\"margin:0 0 8px;color:#666;\">验证码 " + SystemRedisKeys.LOGIN_CODE_TTL
                + " 分钟内有效，请勿泄露给他人。</p>"
                + "<p style=\"margin:0;color:#999;font-size:12px;\">若非本人操作，请忽略本邮件。</p>"
                + "</div>";
    }
}
