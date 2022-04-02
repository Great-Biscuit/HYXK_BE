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
import top.greatbiscuit.hyxk.service.RegisterService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 注册业务实现类
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/19 17:10
 */
@DubboService(version = "v1.0.0")
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisService redisService;

    /**
     * 注册时通过邮箱获取验证码
     *
     * @param email
     * @return
     */
    @Override
    public String getVerificationCode(String email) {

        // 验证邮箱
        User u = userDao.queryByEmail(email);
        if (u != null) {
            return "邮箱已被注册!";
        }

        // 6位验证码
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        // 将验证码放到Redis里存起来(5分钟过期)
        redisService.setCacheObject(RedisKeyUtil.getRegisterCodeKey(email), code, Constants.FIND_PASSWORD_CODE_EXPIRATION, TimeUnit.MINUTES);
        // 发送邮件通知用户
        String text = "验证码为: " + code + "<br/>亲爱的用户, 您正在注册皓月星空站.  该验证码5分钟内有效, 请尽快完成操作." +
                "<br/><br/><br/>" +
                "<div style=\"font-size:60%; color:#b1b3b8\">该邮件由系统自动发出。<br/>" +
                "若您未进行相关操作, 请忽略本邮件, 对您造成打扰, 非常抱歉!</div>";

        // 发布事件发送邮件
        Event event = new Event()
                .setTopic(Constants.TOPIC_SEND_MAIL)
                .setData("to", email)
                .setData("subject", "注册皓月星空站")
                .setData("content", text);
        // 发布
        eventProducer.fireEvent(event);

        return null;
    }

    /**
     * 注册方法
     *
     * @param username
     * @param password
     * @param email
     * @param code     验证码
     * @return
     */
    @Override
    public String toRegister(String username, String password, String email, String code) {

        //空值处理
        if (StringUtils.isBlank(username)) {
            return "用户名为空!";
        }
        if (StringUtils.isBlank(password)) {
            return "密码为空!";
        }
        if (StringUtils.isBlank(email)) {
            return "邮箱为空!";
        }
        if (StringUtils.isBlank(code)) {
            return "验证码为空!";
        }

        // 验证验证码
        String redisKey = RedisKeyUtil.getRegisterCodeKey(email);
        String codeInRedis = redisService.getCacheObject(redisKey).toString();
        // 如果Redis不存在该账号的验证码或者验证码对不上
        if (codeInRedis == null || !codeInRedis.equals(code)) {
            return "验证码错误!";
        }

        // 验证账号
        if (userDao.queryByUsername(username) != null) {
            return "该账号已存在!";
        }

        // 补充其他信息, 进行注册操作
        User user = new User();
        user.setUsername(username);
        // 设置盐值
        String salt = PasswordUtil.generateUUID().substring(0, 5);
        user.setSalt(salt);
        // 对密码进行加密
        user.setPassword(PasswordUtil.md5(password + salt));

        user.setEmail(email);
        // 给定随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        // 昵称
        user.setNickname(username);
        user.setGender(0);
        user.setSignature("Hello World!");
        // 普通用户
        user.setType(0);
        user.setCreateTime(new Date());
        //插入数据库
        userDao.insert(user);

        return null;
    }

}
