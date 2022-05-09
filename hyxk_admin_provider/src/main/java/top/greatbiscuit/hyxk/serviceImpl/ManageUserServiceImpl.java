package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.ManageUserService;
import top.greatbiscuit.hyxk.service.UserService;

/**
 * 管理用户服务生产者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/26 18:59
 */
@DubboService(version = "v1.0.0")
public class ManageUserServiceImpl implements ManageUserService {

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private UserService userService;

    /**
     * 修改用户类型
     *
     * @param userId
     * @param type
     * @return
     */
    @Override
    public String updateUserType(int userId, int type) {
        User user = userService.queryUserById(userId);
        if (user == null) {
            return "用户不存在!";
        }
        user.setType(type);
        userService.updateUserForAdmin(user);
        return null;
    }

}
