package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.PostService;

/**
 * 管理帖子服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/5/1 20:35
 */
@RestController
@RequestMapping("/post")
@ShenyuSpringMvcClient(path = "/post/**")
public class ManagePostController {

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private PostService postService;

    /**
     * 修改帖子状态
     *
     * @param postId
     * @param state  [0-正常 1-加精 2-删除]
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("moderator")
    @PostMapping("/updatePostState")
    public R updatePostState(int postId, int state) {
        String msg = postService.updatePostState(postId, state);
        return msg == null ? R.ok() : R.fail(msg);
    }

    /**
     * 设置帖子置顶
     *
     * @param postId
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("moderator")
    @PostMapping("/setPostTop")
    public R setPostTop(int postId) {
        String msg = postService.setPostTop(postId);
        return msg == null ? R.ok() : R.fail(msg);
    }

    /**
     * 得到帖子数量
     *
     * @param type -1则为所有帖子
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("moderator")
    @PostMapping("/getPostCount")
    public R getPostCount(int type) {
        return R.ok(postService.getPostCount(type));
    }

}
