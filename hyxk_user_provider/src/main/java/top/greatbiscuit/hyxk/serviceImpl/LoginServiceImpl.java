package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.LoginService;
import top.greatbiscuit.hyxk.util.EmailUtil;
import top.greatbiscuit.hyxk.util.PasswordUtil;

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
    private EmailUtil emailUtil;

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

        //将提供的密码进行相同方式的加密
        password = PasswordUtil.md5(password + user.getSalt());

        //验证密码
        if (!password.equals(user.getPassword())) {
            return "密码错误!";
        }

        //激活状态判断
        if (user.getStatus() == 0) {
            return "账号未激活!";
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
        String text = "亲爱的用户, 您正在通过邮箱找回账号。<br/>经系统查询，您的账号为: " + user.getUsername() + "<br/><br/><br/>该邮件由系统自动发出。<br/>" +
                "若您未进行相关操作, 请忽略本邮件, 对您造成打扰, 非常抱歉!";
        //返回邮件发送结果
        return emailUtil.sendMail(email, "找回账号", text);
    }


}
