package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.ManageUserService;

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

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private ManageUserService manageUserService;

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

}
