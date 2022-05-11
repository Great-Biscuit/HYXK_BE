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
     * 找回密码验证码有效期(5分钟)
     */
    public static final long FIND_PASSWORD_CODE_EXPIRATION = 5;

    /**
     * 登录时采取记住我模式
     */
    public static final Integer REMEMBER_ME = 60 * 60 * 24 * 7;

    /**
     * 用户类型[超级管理员]
     */
    public static final Integer USER_TYPE_SUPER_ADMIN = 999;

    /**
     * 用户类型[注销]
     */
    public static final Integer USER_TYPE_DESTROY = 886;

    /**
     * 帖子类型[文章]
     */
    public static final Integer POST_TYPE_ARTICLE = 0;

    /**
     * 帖子类型[公告]
     */
    public static final Integer POST_TYPE_ANNOUNCEMENT = 1;

    /**
     * 帖子类型[问答]
     */
    public static final Integer POST_TYPE_QA = 2;

    /**
     * 帖子类型[表白墙]
     */
    public static final Integer POST_TYPE_CONFESSION_WALL = 3;

    /**
     * 实体类型 帖子
     */
    public static final Integer ENTITY_TYPE_POST = 1;

    /**
     * 实体类型 评论
     */
    public static final Integer ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型 用户
     */
    public static final Integer ENTITY_TYPE_USER = 3;

    /**
     * 主题 评论
     */
    public static final String TOPIC_COMMENT = "comment";

    /**
     * 主题 点赞
     */
    public static final String TOPIC_LIKE = "like";

    /**
     * 主题 关注
     */
    public static final String TOPIC_FOLLOW = "follow";

    /**
     * 主题 收藏
     */
    public static final String TOPIC_COLLECT = "collect";

    /**
     * 主题 发帖
     */
    public static final String TOPIC_PUBLISH = "publish";

    /**
     * 主题 删帖
     */
    public static final String TOPIC_DELETE = "delete";

    /**
     * 主题 发送邮件
     */
    public static final String TOPIC_SEND_MAIL = "sendMail";

    /**
     * 系统用户ID
     */
    public static final Integer SYSTEM_USER_ID = 0;

    /**
     * Quart自动任务时间
     */
    public static final Integer QUARTZ_JOB_TIME = 1000 * 60 * 5; // TODO: 正式上线需要修改时间
}
