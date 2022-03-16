package top.greatbiscuit.hyxk.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
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
@ShenyuSpringMvcClient(path = "/register")
public class RegisterController {

    //记得同时设置网关的超时时间!!!!!!!!!!!!!!!!!!
    @DubboReference(version = "v1.0.0", timeout = 6000)
    private RegisterService registerService;

    /**
     * 进行注册
     *
     * @param username
     * @param password
     * @param email
     * @return
     */
    @RequestMapping("/toRegister")
    @ShenyuSpringMvcClient(path = "/toRegister")
    public R toRegister(String username, String password, String email) {
        String message = registerService.toRegister(username, password, email);
        return message == null ? R.ok() : R.fail(message);
    }

    /**
     * 激活账号
     *
     * @param id
     * @param code
     * @return
     */
    @RequestMapping("/activation")
    @ShenyuSpringMvcClient(path = "/activation")
    public String activation(Integer id, String code) {
        return registerService.activation(id, code);
    }


}
