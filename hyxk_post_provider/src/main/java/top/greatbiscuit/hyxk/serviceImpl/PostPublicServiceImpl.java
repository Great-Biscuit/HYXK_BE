package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.LikeService;
import top.greatbiscuit.hyxk.service.PostPublicService;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公开服务[直接通过网关调用DUBBO服务, 跳过consumer检验操作]
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/19 19:48
 */
@DubboService(version = "v1.0.0")
public class PostPublicServiceImpl implements PostPublicService {

    @Autowired
    private PostDao postDao;

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    @DubboReference(version = "v1.0.0")
    private LikeService likeService;

    /**
     * 查询指定行数据[用户id不为0就查询指定用户, 否则查询所有--先按top排序保证顶置在最前]
     * [orderMode为1则按分数再按时间排序 为0则按时间排序 为2则只按时间排序]
     * [type为-1则查询所有帖子]
     *
     * @param userId
     * @param type
     * @param offset
     * @param limit
     * @param orderMode
     * @return
     */
    @Override
    @ShenyuDubboClient(path = "/queryAllByLimit", desc = "查询帖子列表")
    public List<Map<String, Object>> queryAllByLimit(int userId, int type, int offset, int limit, int orderMode) {
        // 并且此处的帖子信息已被优化, 没必要全部传输
        List<Post> postList = postDao.queryAllByLimit(userId, type, offset, limit, orderMode);
        if (postList == null || postList.size() == 0)
            return null;

        // 把帖子相关信息封装起来传输
        List<Map<String, Object>> posts = new ArrayList<>();
        for (Post post : postList) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            map.put("author", userService.querySimpleUserById(post.getUserId()));
            // 点赞数量
            map.put("likeCount", likeService.findEntityLikeCount(Constants.ENTITY_TYPE_POST, post.getId()));

            posts.add(map);
        }
        return posts;
    }

}
