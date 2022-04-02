package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.CollectService;
import top.greatbiscuit.hyxk.service.LikeService;
import top.greatbiscuit.hyxk.service.SearchService;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/27 17:54
 */
@RestController
@RequestMapping("/search")
@ShenyuSpringMvcClient(path = "/search/**")
public class SearchController {

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private SearchService searchService;

    @DubboReference(version = "v1.0.0", timeout = 6000)
    private LikeService likeService;

    @DubboReference(version = "v1.0.0")
    private CollectService collectService;

    /**
     * 搜索
     *
     * @param keyword 关键字
     * @param current 当前页[从0开始]
     * @param limit   结果数量
     * @param type    搜索类型
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/execute")
    public R search(String keyword, int current, int limit, Integer type) {
        List<Post> postList = searchService.searchPostList(keyword, current, limit, type);
        if (postList == null) {
            return R.ok();
        }
        // 处理结果
        List<Map<String, Object>> searchResult = new ArrayList<>();
        for (Post post : postList) {
            Map<String, Object> map = new HashMap<>();
            // 帖子信息
            map.put("post", post);
            // 作者信息
            map.put("author", userService.querySimpleUserById(post.getUserId()));
            // 点赞数量
            map.put("likeCount", likeService.findEntityLikeCount(Constants.ENTITY_TYPE_POST, post.getId()));
            // 收藏数量
            map.put("collectCount", collectService.findPostCollectCount(post.getId()));

            searchResult.add(map);
        }

        return R.ok(searchResult);
    }

}
