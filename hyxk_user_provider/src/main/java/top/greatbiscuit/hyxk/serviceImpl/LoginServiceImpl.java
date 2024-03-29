package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.event.Event;
import top.greatbiscuit.hyxk.event.EventProducer;
import top.greatbiscuit.hyxk.service.LoginService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import java.util.concurrent.TimeUnit;

/**
 * 登录相关服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/12 20:18
 */
@DubboService(version = "v1.0.0")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisService redisService;

    /**
     * 判断账号密码是否正确
     *
     * @param username 账号
     * @param password 密码
     * @return 验证结果
     */
    @Override
    public String login(String username, String password) {

        //空值处理
        if (StringUtils.isBlank(username)) {
            return "用户名为空!";
        }
        if (StringUtils.isBlank(password)) {
            return "密码为空!";
        }

        //验证账号
        User user = userDao.queryByUsername(username);

        if (user == null) {
            return "账号不存在!";
        }

        // 判断账号是否注销[886为注销]
        if (user.getType() == Constants.USER_TYPE_DESTROY) {
            return "账号已被注销!";
        }

        //将提供的密码进行相同方式的加密
        password = PasswordUtil.md5(password + user.getSalt());

        //验证密码
        if (!password.equals(user.getPassword())) {
            return "密码错误!";
        }

        //成功登录, 返回用户ID用于登录状态标记
        return "ID:" + user.getId();
    }

    /**
     * 通过邮箱找回账号
     *
     * @param email
     * @return
     */
    @Override
    public String findUsername(String email) {
        User user = userDao.queryByEmail(email);
        if (user == null)
            return "用户不存在!";
        // 存在该用户则给用户发送邮件
        String text = "亲爱的用户, 您正在通过邮箱找回账号。<br/>经系统查询，您的账号为: " + user.getUsername() +
                "<br/><br/><br/>" +
                "<div style=\"font-size:60%; color:#b1b3b8\">该邮件由系统自动发出。<br/>" +
                "若您未进行相关操作, 请忽略本邮件, 对您造成打扰, 非常抱歉!</div>";

        // 发布事件发送邮件
        Event event = new Event()
                .setTopic(Constants.TOPIC_SEND_MAIL)
                .setData("to", email)
                .setData("subject", "找回账号")
                .setData("content", text);
        // 发布
        eventProducer.fireEvent(event);

        return null;
    }

    /**
     * 找回密码时通过邮箱获取验证码
     *
     * @param email
     * @return
     */
    @Override
    public String getVerificationCode(String email) {
        User user = userDao.queryByEmail(email);
        if (user == null)
            return "用户不存在!";

        // 存在该用户则给用户发送邮件
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        // 将验证码放到Redis里存起来(5分钟过期)
        redisService.setCacheObject(RedisKeyUtil.getFindPasswordCodeKey(email), code, Constants.FIND_PASSWORD_CODE_EXPIRATION, TimeUnit.MINUTES);
        // 发送邮件通知用户
        String text = "验证码为: " + code + "<br/>您正在找回密码, 该验证码5分钟内有效, 请尽快完成操作." +
                "<br/><br/><br/>" +
                "<div style=\"font-size:60%; color:#b1b3b8\">该邮件由系统自动发出。<br/>" +
                "若您未进行相关操作, 请忽略本邮件, 对您造成打扰, 非常抱歉!</div>";

        // 发布事件发送邮件
        Event event = new Event()
                .setTopic(Constants.TOPIC_SEND_MAIL)
                .setData("to", email)
                .setData("subject", "找回密码")
                .setData("content", text);
        // 发布
        eventProducer.fireEvent(event);

        return null;
    }

    /**
     * 找回密码
     *
     * @param email
     * @param code
     * @param password
     * @return
     */
    @Override
    public String findPassword(String email, String code, String password) {
        String redisKey = RedisKeyUtil.getFindPasswordCodeKey(email);
        // 先判断存不存在, 防止空指针
        if (!redisService.hasKey(redisKey)) {
            return "验证码错误!";
        }
        String codeInRedis = redisService.getCacheObject(redisKey).toString();
        // 如果Redis不存在该账号的验证码或者验证码对不上
        if (codeInRedis == null || !codeInRedis.equals(code)) {
            return "验证码错误!";
        }
        // 否则就进行修改密码的操作
        User user = userDao.queryByEmail(email);
        // 防止操作过程中用户被删除
        if (user == null)
            return "用户不存在!";
        // 加密用户密码并修改
        user.setPassword(PasswordUtil.md5(password + user.getSalt()));
        userDao.update(user);
        // 在Redis里清除验证码
        redisService.deleteObject(redisKey);
        return null;
    }


}
