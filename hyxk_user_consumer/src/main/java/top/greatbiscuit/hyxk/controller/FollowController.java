package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.FollowService;

import java.util.List;
import java.util.Map;

/**
 * 关注服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/24 16:52
 */
@RestController
@RequestMapping("/follow")
@ShenyuSpringMvcClient(path = "/follow/**")
public class FollowController {

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private FollowService followService;

    /**
     * 关注/收藏
     *
     * @param entityType   实体类型
     * @param entityId     实体Id
     * @param entityUserId 实体所属用户
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/toFollow")
    public R follow(int entityType, int entityId, int entityUserId) {
        // 得到当前用户的ID
        int userId = StpUtil.getLoginIdAsInt();
        followService.follow(userId, entityType, entityId, entityUserId);
        return R.ok();
    }

    /**
     * 取消关注/收藏
     *
     * @param entityType 实体类型
     * @param entityId   实体Id
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/unfollow")
    public R unfollow(int entityType, int entityId) {
        // 得到当前用户的ID
        int userId = StpUtil.getLoginIdAsInt();
        followService.unfollow(userId, entityType, entityId);
        return R.ok();
    }

    /**
     * 得到该用户关注了哪些用户
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @RequestMapping("/followList")
    public R getFollowList(int userId, int offset, int limit) {
        // 看当前有没有用户登录[登录就给用户Id赋值]
        Integer holderId = StpUtil.isLogin() ? StpUtil.getLoginIdAsInt() : null;
        // 直接交给生产者处理, 不管是用户不存在还是未关注任何用户都会返回null
        List<Map<String, Object>> followeeInfo = followService.queryFolloweeList(holderId, userId, offset, limit);
        // 将数据返回[没有数据则为空]
        return R.ok(followeeInfo);
    }

    /**
     * 得到该用户的粉丝
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @RequestMapping("/fansList")
    public R getFansList(int userId, int offset, int limit) {
        // 看当前有没有用户登录[登录就给用户Id赋值]
        Integer holderId = StpUtil.isLogin() ? StpUtil.getLoginIdAsInt() : null;
        // 直接交给生产者处理, 不管是用户不存在还是未关注任何用户都会返回null
        List<Map<String, Object>> fansInfo = followService.queryFansList(holderId, userId, offset, limit);
        // 将数据返回[没有数据则为空]
        return R.ok(fansInfo);
    }

}
