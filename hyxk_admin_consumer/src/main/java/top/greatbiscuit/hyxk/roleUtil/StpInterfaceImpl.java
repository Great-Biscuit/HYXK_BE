package top.greatbiscuit.hyxk.roleUtil;

import cn.dev33.satoken.stp.StpInterface;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/26 19:24
 */
@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        int type = userService.queryUserType(Integer.valueOf(loginId.toString()));
        List<String> list = new ArrayList<String>();
        if (type == Constants.USER_TYPE_SUPER_ADMIN) {
            // 超级管理员
            list.add("admin");
        } else if (type != Constants.USER_TYPE_DESTROY && type != 0) {
            // 用户未被销毁且不是普通用户[则为版主]
            list.add("moderator");
        }
        return list;
    }

}
