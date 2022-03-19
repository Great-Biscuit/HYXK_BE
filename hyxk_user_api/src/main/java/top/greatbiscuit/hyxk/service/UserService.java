package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.User;

import java.util.Map;

/**
 * 用户表(User)表服务接口
 *
 * @Author GreatBiscuit
 * @Date 2022-01-10 15:30:25
 */
public interface UserService {


    /**
     * 修改密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Map updatePassword(Integer userId, String oldPassword, String newPassword);

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    String updateUser(User user);

    /**
     * 根据ID查询用户
     *
     * @param userId
     * @return
     */
    User queryUserById(Integer userId);
}
