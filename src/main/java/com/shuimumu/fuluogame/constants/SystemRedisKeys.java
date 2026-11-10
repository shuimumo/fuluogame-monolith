package com.shuimumu.fuluogame.constants;

public class SystemRedisKeys {
    /** 邮箱验证码登录：验证码 key（email 已归一化为小写） */
    public static String loginCode(String email)  { return "login:code:" + email; }
    /** 邮箱验证码登录：发送间隔 key */
    public static String loginCodeInterval(String email) { return "login:code:interval:" + email; }
    /** 登录失败计数 key（按邮箱账号） */
    public static String loginFail(String account) { return "login:fail:" + account; }
    /** 登录锁定 key（按邮箱账号） */
    public static String loginBlock(String account) { return "login:block:" + account; }
    /** 邮箱验证码登录：单邮箱每日发送上限 key */
    public static String loginCodeDailyCount(String email) { return "login:code:daily:" + email; }
    public static String loginToken(String token) { return "login:token:" + token; }
    public static String cachePostContent(Long id) { return "cache:postContent:" + id; }
    public static String cacheUser(Long id)      { return "cache:user:" + id; }
    public static String cacheUserProfile(Long id) { return "cache:userProfile:" + id; }
    public static String cacheCommentContent(Long id) { return "cache:comment:" + id; }
    public static String icr(String type, String date) { return "icr:" + type + ":" + date; }
    public static String cacheBanners()       { return "cache:banners"; }
    public static String cacheAnnouncements() { return "cache:announcements"; }
    public static String cacheAiSummarise(Long postId) { return "cache:ai:summarise:" + postId; }
    public static final long AI_SUMMARISE_TTL = 30;

    public static final long LOGIN_CODE_TTL = 5;
    public static final long LOGIN_CODE_INTERVAL_TTL = 60;
    /** 验证码/密码连续失败上限，超过后锁定 LOGIN_FAIL_LOCK_MINUTES 分钟 */
    public static final long LOGIN_FAIL_MAX = 5;
    public static final long LOGIN_FAIL_LOCK_MINUTES = 10;
    /** 单邮箱每日验证码发送上限（防邮件轰炸） */
    public static final long LOGIN_CODE_DAILY_MAX = 20;
    public static final long LOGIN_USER_TTL = 30;
}