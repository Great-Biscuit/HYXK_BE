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

import java.util.HashMap;
import java.util.Map;

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

    @DubboReference(version = "v1.0.0", timeout = 10000)
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
     * @return
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/getPostCount")
    public R getPostCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("allPost", postService.getPostCount(-1));
        map.put("article", postService.getPostCount(0));
        map.put("announcement", postService.getPostCount(1));
        map.put("QA", postService.getPostCount(2));
        map.put("confessionWall", postService.getPostCount(3));
        return R.ok(map);
    }

}
