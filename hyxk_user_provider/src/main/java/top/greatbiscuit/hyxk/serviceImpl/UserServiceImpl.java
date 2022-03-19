package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.UserService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/10 15:58
 */
@DubboService(version = "v1.0.0")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 修改密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public Map updatePassword(Integer userId, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        User user = userDao.queryById(userId);

        //将提供的密码进行相同方式的加密
        oldPassword = PasswordUtil.md5(oldPassword + user.getSalt());

        //验证密码
        if (!oldPassword.equals(user.getPassword())) {
            map.put("passwordMsg", "密码错误!");
            return map;
        }

        //旧密码正确, 则修改密码
        user.setPassword(newPassword);
        userDao.update(user);

        return map;
    }

    /**
     * 修改用户信息
     *
     * @param user
     */
    @Override
    public String updateUser(User user) {
        User u = userDao.queryById(user.getId());
        if (u == null) {
            return "用户不存在!";
        }
        // 只有部分信息可以修改
        u.setHeaderUrl(user.getHeaderUrl());
        u.setNickname(user.getNickname());
        u.setGender(user.getGender());
        u.setSignature(user.getSignature());

        userDao.update(u);
        return null;
    }


    /**
     * 根据ID查询用户
     *
     * @param userId
     * @return
     */
    @Override
    public User queryUserById(Integer userId) {
        return userDao.queryById(userId);
    }
}
