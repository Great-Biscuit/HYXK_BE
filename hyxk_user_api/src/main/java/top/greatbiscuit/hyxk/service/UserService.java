package top.greatbiscuit.hyxk.service;

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
     * 修改昵称
     *
     * @param userId
     * @param nickname
     */
    void updateNickname(Integer userId, String nickname);

    /**
     * 修改性别
     *
     * @param userId
     * @param gender
     */
    void updateGender(Integer userId, Integer gender);
}
