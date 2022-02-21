package top.greatbiscuit.hyxk.controller.exceptionHandler;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.Map;

/**
 * 用户信息设置类
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/20 20:20
 */
@RestController
@RequestMapping("/set")
@ShenyuSpringMvcClient(path = "/set")
public class UserController {

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @SaCheckLogin
    @RequestMapping("/updatePassword")
    @ShenyuSpringMvcClient(path = "/updatePassword")
    public R updatePassword(String oldPassword, String newPassword) {
        Map map = userService.updatePassword(StpUtil.getLoginIdAsInt(), oldPassword, newPassword);
        if (!map.isEmpty()) return R.fail(map);
        return R.ok("密码修改成功!");
    }

    /**
     * 修改昵称
     *
     * @param nickname 昵称
     * @return 修改结果
     */
    @SaCheckLogin
    @RequestMapping("/updateNickname")
    @ShenyuSpringMvcClient(path = "/updateNickname")
    public R updateNickname(String nickname) {
        if (nickname.length() > 16) return R.fail("昵称过长!");
        //将nickname进行html转义
        userService.updateNickname(StpUtil.getLoginIdAsInt(), HtmlUtils.htmlEscape(nickname));
        return R.ok("昵称修改成功!");
    }

    /**
     * 修改性别
     *
     * @param gender 性别标记值
     * @return 修改结果
     */
    @SaCheckLogin
    @RequestMapping("/updateGender")
    @ShenyuSpringMvcClient(path = "/updateGender")
    public R updateGender(Integer gender) {
        if (gender > 2) return R.fail("请输入正确的性别标记值!");
        userService.updateGender(StpUtil.getLoginIdAsInt(), gender);
        return R.ok("性别修改成功!");
    }


}
