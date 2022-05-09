package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.ManageUserService;
import top.greatbiscuit.hyxk.service.UserService;

/**
 * 管理用户服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/26 18:34
 */
@RestController
@RequestMapping("/user")
@ShenyuSpringMvcClient(path = "/user/**")
public class ManageUserController {

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private ManageUserService manageUserService;

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private UserService userService;

    /**
     * 查询用户列表
     *
     * @param offset
     * @param limit
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/getUserListByLimit")
    public R getUserListByLimit(int offset, int limit) {
        return R.ok(userService.getUserListByLimit(offset, limit));
    }

    /**
     * 修改用户类型
     *
     * @param userId
     * @param type
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/updateType")
    public R updateUserType(int userId, int type) {
        String msg = manageUserService.updateUserType(userId, type);
        return msg == null ? R.ok() : R.fail(msg);
    }

    /**
     * 封禁用户
     *
     * @param userId
     * @param banTime 封禁时长，单位：秒  (86400秒=1天，此值为-1时，代表永久封禁)
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/banUser")
    public R banUser(int userId, long banTime) {
        // 先踢下线
        StpUtil.kickout(userId);
        StpUtil.disable(userId, banTime);
        return R.ok();
    }

    /**
     * 是否被封禁
     *
     * @param userId
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/hasBan")
    public R hasBan(int userId) {
        return R.ok(StpUtil.isDisable(userId));
    }

    /**
     * 解封
     *
     * @param userId
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/unBanUser")
    public R unBanUser(int userId) {
        StpUtil.untieDisable(userId);
        return R.ok();
    }

}
