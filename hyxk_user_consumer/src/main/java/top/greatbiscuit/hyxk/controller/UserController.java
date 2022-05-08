package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.qiniu.util.Auth;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.FollowService;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.HashMap;
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

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private FollowService followService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询当前用户Id[不存在则返回空]
     *
     * @return
     */
    @RequestMapping("/getHolderUserId")
    public R getHolderUserId() {
        if (StpUtil.isLogin()) {
            // 如果已经登录
            return R.ok(StpUtil.getLoginIdAsInt());
        } else {
            // 如果未登录
            return R.ok();
        }
    }

    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/getUserInfo/{userId}")
    public R getUserInfo(@PathVariable("userId") int userId) {
        User user = userService.querySimpleUserById(userId);
        if (user == null) {
            return R.fail("用户不存在!");
        }

        // 构造返回数据
        Map<String, Object> userInfo = new HashMap<>();
        // 装入用户信息
        userInfo.put("user", user);
        // 装入被赞数
        userInfo.put("beLikedCount", userService.findUserLikeCount(userId));
        // 装入关注用户数
        userInfo.put("followCount", followService.queryFollowCount(userId));
        // 装入粉丝数
        userInfo.put("fansCount", followService.queryFansCount(userId));

        if (StpUtil.isLogin()) {
            int holderUserId = StpUtil.getLoginIdAsInt();
            // 当前用户是否关注
            userInfo.put("hasFollow", followService.hasFollowed(holderUserId, Constants.ENTITY_TYPE_USER, userId));
            // 已登录就把当前用户Id也返回
            return R.ok(userInfo, holderUserId + "");
        } else {
            // 未登录就判定为未关注
            userInfo.put("hasFollow", false);
            // 未登录就只返回查询的用户信息
            return R.ok(userInfo);
        }
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @SaCheckLogin
    @PostMapping("/updatePassword")
    public R updatePassword(String oldPassword, String newPassword) {
        Map map = userService.updatePassword(StpUtil.getLoginIdAsInt(), oldPassword, newPassword);
        if (!map.isEmpty()) return R.fail(map);
        return R.ok("密码修改成功!");
    }

    /**
     * 得到当前用户的信息[用于修改用户信息]
     *
     * @return
     */
    @SaCheckLogin
    @PostMapping("/getHolderInfo")
    public R getHolderInfo() {
        return R.ok(userService.queryUserById(StpUtil.getLoginIdAsInt()));
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
        if (user == null) return R.fail("未获取到用户信息!");
        // 基础处理
        if (user.getNickname().length() > 15) return R.fail("昵称过长!");
        if (user.getGender() < 0 || user.getGender() > 2) return R.fail("请选择正确的性别类型!");
        // 将nickname signature进行html转义
        user.setNickname(HtmlUtils.htmlEscape(user.getNickname()));
        user.setSignature(HtmlUtils.htmlEscape(user.getSignature()));
        // 设置用户编号
        user.setId(StpUtil.getLoginIdAsInt());
        String msg = userService.updateUser(user);
        return msg == null ? R.ok() : R.fail(msg);
    }

    /**
     * 得到上传头像的凭证
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/getUploadAvatarToken")
    public R getUploadAvatarToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        // 过期时间
        long expireSeconds = 60 * 60 * 3;
        return R.ok(auth.uploadToken(bucket, null, expireSeconds, null));
    }

}
