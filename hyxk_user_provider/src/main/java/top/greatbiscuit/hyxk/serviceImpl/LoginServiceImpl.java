package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.LoginService;
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


}
