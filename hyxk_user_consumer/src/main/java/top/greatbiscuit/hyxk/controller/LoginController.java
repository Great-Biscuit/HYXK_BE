package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.constant.Constants;
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
    public R verify(String username, String password, boolean rememberMe) {
        Map<String, Object> map = loginService.login(username, password);

        //如果map中只存在用户ID一个数据，说明是成功登录
        if (map.containsKey("UserID")) {
            //将用户登录状态进行改变
            //选择记住我则登录状态保持一周, 否则保持十分钟
            StpUtil.login(map.get("UserID"), new SaLoginModel()
                    .setTimeout(rememberMe ? Constants.REMEMBER_ME : Constants.NOT_REMEMBER_ME));
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
    @SaCheckLogin
    @RequestMapping("/loginOut")
    @ShenyuSpringMvcClient(path = "/loginOut")
    public R loginOut() {
        StpUtil.logout();
        return R.ok();
    }

}
