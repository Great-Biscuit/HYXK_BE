package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.LikeService;

/**
 * 点赞服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/20 16:03
 */
@RestController
@RequestMapping("/like")
@ShenyuSpringMvcClient(path = "/like/**")
public class LikeController {

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private LikeService likeService;

    /**
     * 点赞[取消点赞也是调用该方法, 在业务层进行判断]
     *
     * @param entityType   被点赞的实体类型
     * @param entityId     被点赞的实体ID
     * @param entityUserId 被点赞的实体的发布者
     * @param postId       帖子Id, 让前端传入, 减少数据库交互次数
     * @return
     */
    @SaCheckLogin
    @PostMapping("/execute")
    public R like(int entityType, int entityId, int entityUserId, int postId) {
        int userId = StpUtil.getLoginIdAsInt();
        String msg = likeService.like(userId, entityType, entityId, entityUserId, postId);
        return msg == null ? R.ok() : R.fail(msg);
    }

}
