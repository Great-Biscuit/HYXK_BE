package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.qiniu.util.Auth;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.CollectService;
import top.greatbiscuit.hyxk.service.PostService;

import java.util.List;
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

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private PostService postService;

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private CollectService collectService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    /**
     * 新增帖子
     *
     * @param title
     * @param markdownContent
     * @param headerUrl
     * @param type
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

    /**
     * 查询用户收藏的帖子列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @RequestMapping("/getCollectedPostList")
    public R getCollectedPostList(int userId, int offset, int limit) {
        List<Map<String, Object>> collectedPostList = collectService.getCollectedPostList(userId, offset, limit);
        return R.ok(collectedPostList);
    }

    /**
     * 查询待编辑的帖子信息
     *
     * @param postId
     * @return
     */
    @SaCheckLogin
    @GetMapping("/getPostForUpdate/{postId}")
    public R getPostForUpdate(@PathVariable("postId") int postId) {
        Post post = postService.getPostForUpdate(StpUtil.getLoginIdAsInt(), postId);
        return post == null ? R.fail("获取帖子信息出错!") : R.ok(post);
    }

    /**
     * 修改帖子
     *
     * @param id
     * @param title
     * @param markdownContent
     * @param headerUrl
     * @param type
     * @return
     */
    @SaCheckLogin
    @PostMapping("/updatePost")
    public R updatePost(Integer id, String title, String markdownContent, String headerUrl, Integer type) {
        Post post = new Post();
        post.setId(id);
        // 当前登录的用户ID就是帖子作者 后面再判断对不对
        Integer userId = StpUtil.getLoginIdAsInt();
        post.setUserId(userId);
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(title));
        post.setMarkdownContent(markdownContent);
        post.setHeadImg(headerUrl);
        post.setType(type);

        String msg = postService.updatePost(post);
        return msg == null ? R.ok("修改成功!") : R.fail(msg);
    }

    /**
     * 得到上传图片的凭证
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/getUploadImageToken")
    public R getUploadImageToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        // 过期时间
        long expireSeconds = 60 * 60 * 10;
        return R.ok(auth.uploadToken(bucket, null, expireSeconds, null));
    }

}
