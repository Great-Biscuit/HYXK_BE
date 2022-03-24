package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.event.Event;
import top.greatbiscuit.hyxk.event.EventProducer;
import top.greatbiscuit.hyxk.service.RegisterService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import java.util.Date;
import java.util.Random;

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

    /**
     * 注册方法
     *
     * @param username
     * @param password
     * @param email
     * @return
     */
    @Override
    public String toRegister(String username, String password, String email) {

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

        // 验证账号
        User u = userDao.queryByUsername(username);
        if (u != null) {
            return "该账号已存在!";
        }

        //验证邮箱
        u = userDao.queryByEmail(email);
        if (u != null) {
            return "邮箱已被注册!";
        }

        //补充其他信息, 进行注册操作
        User user = new User();
        user.setUsername(username);
        //设置盐值
        String salt = PasswordUtil.generateUUID().substring(0, 5);
        user.setSalt(salt);
        //对密码进行加密
        user.setPassword(PasswordUtil.md5(password + salt));

        user.setEmail(email);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setNickname(username);
        user.setGender(0);
        user.setSignature("Hello World!");
        user.setType(0);
        //设置其状态为未激活
        user.setStatus(0);
        //设置激活码
        user.setActivationCode(PasswordUtil.generateUUID());

        user.setCreateTime(new Date());
        //插入数据库
        userDao.insert(user);

        // 给用户发送激活邮件
        String url = "http://localhost:9195" + "/user/register/activation?id=" + user.getId() + "&code=" + user.getActivationCode();
        String text = "    亲爱的" + username + "用户, 您好! 欢迎注册皓月星空站, 请点击<a href=\"" + url + "\">链接</a>以激活账号。" +
                "<br/><br/>" +
                "若点击上述链接无法进行跳转, 请将下面的地址复制至浏览器打开: <br/>" +
                url +
                "<br/><br/><br/>" +
                "<div style=\"font-size:60%; color:#b1b3b8\">该邮件由系统自动发出。<br/>" +
                "若您未进行相关操作, 请忽略本邮件, 对您造成打扰, 非常抱歉!</div>";

        // 发布事件发送邮件
        Event event = new Event()
                .setTopic(Constants.TOPIC_SEND_MAIL)
                .setData("to", email)
                .setData("subject", "激活账号")
                .setData("content", text);
        // 发布
        eventProducer.fireEvent(event);

        return null;
    }

    /**
     * 激活账号
     *
     * @param userId
     * @param code
     * @return
     */
    @Override
    public String activation(Integer userId, String code) {
        User user = userDao.queryById(userId);
        if (user.getStatus() == 1) {
            return "请勿重复激活!";
        } else if (code.equals(user.getActivationCode())) {
            user.setStatus(1);
            userDao.update(user);
            return "激活成功!";
        } else {
            return "激活码错误!";
        }
    }
}
