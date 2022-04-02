package top.greatbiscuit.hyxk.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.RegisterService;

/**
 * 注册控制类
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/19 17:13
 */
@RestController
@RequestMapping("/register")
@ShenyuSpringMvcClient(path = "/register/**")
public class RegisterController {

    //记得同时设置网关的超时时间!!!!!!!!!!!!!!!!!!
    @DubboReference(version = "v1.0.0", timeout = 6000)
    private RegisterService registerService;

    /**
     * 注册时通过邮箱获取验证码
     *
     * @param email
     * @return
     */
    @RequestMapping("/getVerificationCode")
    public R getVerificationCode(String email) {
        String msg = registerService.getVerificationCode(email);
        return msg == null ? R.ok() : R.fail(msg);
    }

    /**
     * 进行注册
     *
     * @param username
     * @param password
     * @param email
     * @param code
     * @return
     */
    @PostMapping("/toRegister")
    public R toRegister(String username, String password, String email, String code) {
        String message = registerService.toRegister(username, password, email, code);
        return message == null ? R.ok() : R.fail(message);
    }

}
