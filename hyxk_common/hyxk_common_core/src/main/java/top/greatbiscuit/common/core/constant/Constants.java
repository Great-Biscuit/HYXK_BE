package top.greatbiscuit.common.core.constant;

/**
 * 通用常量信息
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/12 20:08
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 找回密码验证码
     */
    public static final String FIND_PASSWORD_CODE = "find_password_code:";

    /**
     * 找回密码验证码有效期(5分钟)
     */
    public static final long FIND_PASSWORD_CODE_EXPIRATION = 5;

    /**
     * 登录时采取记住我模式
     */
    public static final Integer REMEMBER_ME = 60 * 60 * 24 * 7;

    /**
     * 登录时 不 采取记住我模式
     */
    public static final Integer NOT_REMEMBER_ME = 60 * 10;

}
