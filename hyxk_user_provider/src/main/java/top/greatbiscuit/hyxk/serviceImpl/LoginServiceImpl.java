package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.LoginService;
import top.greatbiscuit.hyxk.service.UserService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录相关服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/12 20:18
 */
@DubboService(version = "v1.0.0")
public class LoginServiceImpl implements LoginService {

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    /**
     * 判断账号密码是否正确
     *
     * @param username 账号
     * @param password 密码
     * @return 验证结果
     */
    @Override
    public Map login(String username, String password) {

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

        //验证账号
        User user = userService.queryByUsername(username);

        if (user == null) {
            map.put("usernameMsg", "账号不存在!");
            return map;
        }

        //将提供的密码进行相同方式的加密
        password = PasswordUtil.md5(password + user.getSalt());
        System.out.println(password);

        //验证密码
        if (!password.equals(user.getPassword())) {
            map.put("passwordMsg", "密码错误!");
            return map;
        }

        //激活状态判断
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活!");
            return map;
        }

        //成功登录, 返回用户ID用于登录状态标记
        map.put("UserID", user.getId());

        return map;
    }


}
