package top.greatbiscuit.hyxk.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.UserService;

/**
 * @Author: GreatBiscuit
 * @Date: 2022/1/10 17:36
 */
@RestController
public class UserController {

    @DubboReference
    private UserService userService;

    @RequestMapping("/selectUser")
    public User selectUser(@RequestParam(name = "id") Integer id) {
        return userService.queryById(id);
    }

}
