package com.shuimumu.fuluogame.controller;

import cn.hutool.core.util.StrUtil;
import com.shuimumu.fuluogame.constants.SystemRedisKeys;
import com.shuimumu.fuluogame.dto.LoginDTO;
import com.shuimumu.fuluogame.dto.SendCodeDTO;
import com.shuimumu.fuluogame.interceptor.UserContext;
import com.shuimumu.fuluogame.service.LoginService;
import com.shuimumu.fuluogame.utils.EmailUtils;
import com.shuimumu.fuluogame.utils.RegexUtils;
import com.shuimumu.fuluogame.vo.Result;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户相关接口")
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final int MAX_FAILS = 5;
    private static final int BLOCK_MINUTES = 30;

    private boolean isBlocked(String account) {
        return stringRedisTemplate.hasKey(SystemRedisKeys.loginBlock(account));
    }

    private void recordFail(String account) {
        String key = SystemRedisKeys.loginFail(account);
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) stringRedisTemplate.expire(key, 15, TimeUnit.MINUTES);
        if (count != null && count >= MAX_FAILS) {
            stringRedisTemplate.opsForValue().set(SystemRedisKeys.loginBlock(account), "1",
                    BLOCK_MINUTES, TimeUnit.MINUTES);
            stringRedisTemplate.delete(key);
        }
    }

    private void clearFails(String account) {
        stringRedisTemplate.delete(SystemRedisKeys.loginFail(account));
        stringRedisTemplate.delete(SystemRedisKeys.loginBlock(account));
    }

    @Operation(summary = "邮箱验证码登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String email = EmailUtils.normalize(loginDTO.getEmail());
        if (RegexUtils.isEmailInvalid(email)) return Result.error("邮箱格式错误");
        if (isBlocked(email)) return Result.error("尝试次数过多，请30分钟后重试");
        try {
            String token = loginService.login(loginDTO);
            clearFails(email);
            return Result.success(Map.of("token", token));
        } catch (Exception e) {
            recordFail(email);
            throw e;
        }
    }

    @Operation(summary = "发送验证码（邮箱）")
    @PostMapping("/sendCode")
    public Result sendCode(@RequestBody SendCodeDTO sendCodeDTO) {
        String email = EmailUtils.normalize(sendCodeDTO.getEmail());
        if (StrUtil.isBlank(email)) return Result.error("邮箱不能为空");
        if (RegexUtils.isEmailInvalid(email)) return Result.error("邮箱格式错误");
        loginService.sendCode(email);
        return Result.success();
    }

    @Operation(summary = "邮箱密码登录")
    @PostMapping("/login/password")
    public Result loginByPassword(@RequestBody LoginDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) return Result.error("密码不能为空");
        String email = EmailUtils.normalize(dto.getEmail());
        if (RegexUtils.isEmailInvalid(email)) return Result.error("邮箱格式错误");
        if (isBlocked(email)) return Result.error("尝试次数过多，请30分钟后重试");
        try {
            Map<String, Object> result = loginService.loginByPassword(email, dto.getPassword());
            clearFails(email);
            return Result.success(result);
        } catch (Exception e) {
            recordFail(email);
            throw e;
        }
    }

    @Operation(summary = "设置密码")
    @PutMapping("/password")
    public Result setPassword(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.isEmpty()) return Result.error("密码不能为空");
        loginService.setPassword(UserContext.getUser().getId(), password);
        return Result.success();
    }
}
