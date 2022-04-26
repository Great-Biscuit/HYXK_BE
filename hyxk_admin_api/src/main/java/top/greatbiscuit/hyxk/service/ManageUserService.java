package top.greatbiscuit.hyxk.service;

/**
 * 管理用户服务接口
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/26 18:56
 */
public interface ManageUserService {

    /**
     * 修改用户类型
     *
     * @param userId
     * @param type
     * @return
     */
    String updateUserType(int userId, int type);

}
