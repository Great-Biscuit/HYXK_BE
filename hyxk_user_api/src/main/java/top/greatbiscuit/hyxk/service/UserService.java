package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.User;

import java.util.List;
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
     * 修改所有用户信息[用于后台管理]
     *
     * @param user
     */
    String updateUserForAdmin(User user);

    /**
     * 根据ID查询用户
     *
     * @param userId
     * @return
     */
    User queryUserById(Integer userId);

    /**
     * 根据ID查询出用户类型
     *
     * @param userId
     * @return
     */
    Integer queryUserType(Integer userId);

    /**
     * 查询出简略的用户信息[id nickname headerUrl type]
     *
     * @param userId
     * @return
     */
    User querySimpleUserById(Integer userId);

    /**
     * 用户被点赞总数
     *
     * @param userId
     * @return
     */
    int findUserLikeCount(int userId);

    /**
     * 查询用户列表
     *
     * @param offset
     * @param limit
     * @return
     */
    List<User> getUserListByLimit(int offset, int limit);
}
