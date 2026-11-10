package com.shuimumu.fuluogame.utils;

import cn.hutool.core.util.StrUtil;

/**
 * 邮箱账号处理工具：登录/发信统一使用「去空格 + 小写」后的邮箱，避免大小写和空格导致缓存 key 不一致。
 */
public class EmailUtils {

    private EmailUtils() {
    }

    /**
     * 归一化邮箱账号，非法输入（空白）返回 null。
     */
    public static String normalize(String email) {
        return StrUtil.isBlank(email) ? null : email.trim().toLowerCase();
    }
}
