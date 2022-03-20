package top.greatbiscuit.hyxk.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.PostService;

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

    @DubboReference(version = "v1.0.0")
    private PostService postService;

    /**
     * 新增帖子
     *
     * @param title
     * @param markdownContent
     * @param headerUrl
     * @return
     */
    //@SaCheckLogin
    @RequestMapping("/addPost")
    public R addPost(String title, String markdownContent, String headerUrl, Integer type) {
        System.out.println(type);
        Post post = new Post();
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(title));

        // TODO: 改回真正的发帖用户并开启登录验证
        //Integer userId = StpUtil.getLoginIdAsInt();
        //post.setUserId(userId);
        post.setUserId(1);
        post.setMarkdownContent(markdownContent);
        post.setHeadImg(headerUrl);
        post.setType(type);
        // TODO: 查询出用户类型并赋值
        post.setOfficial(0);

        String msg = postService.insertPost(post);
        return msg == null ? R.ok("发布成功!") : R.fail(msg);
    }

}
