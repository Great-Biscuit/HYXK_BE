package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.PostService;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.Map;

/**
 * 帖子服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/20 16:03
 */
@RestController
@RequestMapping("/action")
@ShenyuSpringMvcClient(path = "/action/**")
public class PostController {

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private PostService postService;

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    /**
     * 新增帖子
     *
     * @param title
     * @param markdownContent
     * @param headerUrl
     * @return
     */
    @SaCheckLogin
    @PostMapping("/addPost")
    public R addPost(String title, String markdownContent, String headerUrl, Integer type) {
        Post post = new Post();
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(title));

        // 当前登录的用户ID就是帖子作者
        Integer userId = StpUtil.getLoginIdAsInt();
        post.setUserId(userId);
        post.setMarkdownContent(markdownContent);
        post.setHeadImg(headerUrl);
        post.setType(type);
        // 如果是普通用户就是非官方, 否则就是官方
        post.setOfficial(userService.queryUserType(userId) != 0 ? 1 : 0);

        String msg = postService.insertPost(post);
        return msg == null ? R.ok("发布成功!") : R.fail(msg);
    }

    /**
     * 查询一条帖子的所有数据
     *
     * @param postId
     * @return
     */
    @RequestMapping("/detail/{postId}")
    public R getPost(@PathVariable("postId") int postId) {
        // 得到当前用户的ID
        Integer userId = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsInt();
        }

        Map<String, Object> postDetail = postService.queryPostDetailById(postId, userId);
        if (postDetail == null) {
            R.fail("查询帖子信息出现错误!");
        }

        // 如果当前用户已登录则将用户的ID也返回, 用于前端处理
        return userId == null ? R.ok(postDetail) : R.ok(postDetail, userId.toString());
    }

    /**
     * 删除帖子
     *
     * @param postId
     * @return
     */
    @SaCheckLogin
    @PostMapping("/deletePost")
    public R deletePost(int postId) {
        String msg = postService.deletePost(StpUtil.getLoginIdAsInt(), postId);
        return msg == null ? R.ok() : R.fail(msg);
    }

}
