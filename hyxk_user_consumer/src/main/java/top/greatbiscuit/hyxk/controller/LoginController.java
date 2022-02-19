package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.LoginService;

import java.util.Map;

/**
 * 登录控制类
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/21 16:21
 */
@RestController
@RequestMapping("/login")
@ShenyuSpringMvcClient(path = "/login")
public class LoginController {

    @DubboReference(version = "v1.0.0")
    private LoginService loginService;

    /**
     * 验证登录信息
     *
     * @param username 账号
     * @param password 密码
     * @return 成功处理
     */
    @PostMapping("/verify")
    @ShenyuSpringMvcClient(path = "/verify")
    public R verify(String username, String password) {
        Map<String, Object> map = loginService.login(username, password);

        //如果map中只存在用户ID一个数据，说明是成功登录
        if (map.containsKey("UserID")) {
            //将用户登录状态进行改变
            //登录状态保持10分钟
            StpUtil.login(map.get("UserID"), new SaLoginModel().setTimeout(60 * 10));
            return R.ok();
        } else {
            //否则就是登录失败
            return R.fail(map);
        }
    }

    /**
     * 退出登录
     *
     * @return 成功处理
     */
    @RequestMapping("/loginOut")
    @ShenyuSpringMvcClient(path = "/loginOut")
    public R loginOut() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        return R.ok();
    }

}
