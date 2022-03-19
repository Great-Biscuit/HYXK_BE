package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.Map;

/**
 * 用户信息操作类
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/20 20:20
 */
@RestController
@RequestMapping("/action")
@ShenyuSpringMvcClient(path = "/action/**")
public class UserController {

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    /**
     * 查询当前用户信息
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/getMyself")
    public R getMyself() {
        User user = userService.queryUserById(StpUtil.getLoginIdAsInt());
        return user == null ? R.fail("错误!") : R.ok(user);
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @SaCheckLogin
    @RequestMapping("/updatePassword")
    public R updatePassword(String oldPassword, String newPassword) {
        Map map = userService.updatePassword(StpUtil.getLoginIdAsInt(), oldPassword, newPassword);
        if (!map.isEmpty()) return R.fail(map);
        return R.ok("密码修改成功!");
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/updateInfo")
    public R updateInfo(User user) {
        if (user == null) return R.fail("为获取到用户信息!");
        // 基础处理
        if (user.getNickname().length() > 20) return R.fail("昵称过长!");
        if (user.getGender() < 0 || user.getGender() > 2) return R.fail("请选择正确的性别类型!");
        // 将nickname signature进行html转义
        user.setNickname(HtmlUtils.htmlEscape(user.getNickname()));
        user.setSignature(HtmlUtils.htmlEscape(user.getSignature()));
        String msg = userService.updateUser(user);
        return msg == null ? R.ok("昵称修改成功!") : R.fail(msg);
    }


}
