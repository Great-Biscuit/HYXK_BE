package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.RegisterService;
import top.greatbiscuit.hyxk.service.UserService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 注册业务实现类
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/19 17:10
 */
@DubboService(version = "v1.0.0")
public class RegisterServiceImpl implements RegisterService {

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 注册方法
     *
     * @param username
     * @param password
     * @param email
     * @return
     */
    @Override
    public Map toRegister(String username, String password, String email) {

        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码为空!");
            return map;
        }
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱为空!");
            return map;
        }

        // 验证账号
        User u = userDao.queryByUsername(username);
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        //验证邮箱
        u = userDao.queryByEmail(email);
        if (u != null) {
            map.put("emailMsg", "邮箱已被注册!");
            return map;
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

        // 给用户发送激活邮件[需求相对简单, 直接嵌入相关代码]
        //------------------------------------------------------------------------------
        try {
            String url = "http://localhost:9195" + "/user/register/activation?id=" + user.getId() + "&code=" + user.getActivationCode();
            String text = "    亲爱的" + username + "用户, 您好! 欢迎注册皓月星空站, 请点击<a href=\"" + url + "\">链接</a>以激活账号。";
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("hyxk_station@foxmail.com");
            helper.setTo(email);
            helper.setSubject("激活账号");
            helper.setText(text, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            map.put("sendMailMsg", "发送邮件出错!");
            return map;
        }
        //------------------------------------------------------------------------------

        //成功注册
        return map;
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
