package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.entity.Comment;
import top.greatbiscuit.hyxk.service.CommentService;

/**
 * 评论服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/20 16:05
 */
@RestController
@RequestMapping("/comment")
@ShenyuSpringMvcClient(path = "/comment/**")
public class CommentController {

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private CommentService commentService;

    @SaCheckLogin
    @PostMapping("/addComment")
    public R addComment(Comment comment) {
        comment.setUserId(StpUtil.getLoginIdAsInt());
        // 进行Html转义
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        String msg = commentService.addComment(comment);
        return msg == null ? R.ok() : R.fail(msg);
    }

    @SaCheckLogin
    @PostMapping("/deleteComment")
    public R deleteComment(int commentId, int postId) {
        String msg = commentService.deleteComment(commentId, postId, StpUtil.getLoginIdAsInt());
        return msg == null ? R.ok() : R.fail(msg);
    }

    /**
     * 设置最佳评论
     *
     * @param commentId
     * @param postId
     * @return
     */
    @SaCheckLogin
    @PostMapping("/setBestComment")
    public R setBestComment(int commentId, int postId) {
        String msg = commentService.setBestComment(StpUtil.getLoginIdAsInt(), commentId, postId);
        return msg == null ? R.ok() : R.fail(msg);
    }

}
