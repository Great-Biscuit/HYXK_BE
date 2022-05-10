package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.LikeService;
import top.greatbiscuit.hyxk.service.PostPublicService;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.*;

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

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private UserService userService;

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private LikeService likeService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询指定行数据[用户id不为0就查询指定用户, 否则查询所有--先按top排序保证顶置在最前]
     * [orderMode为1则按分数再按时间排序 为0则按时间排序 为2则只按时间排序 为-1则为关注]
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
        // 关注
        if (orderMode == -1) {
            return queryFolloweeNotes(userId, offset, limit);
        }
        // 问答区分是否已解答 0全部 1已解决 2未解决
        if (type == 2) {
            return queryQAByState(orderMode, offset, limit);
        }
        // 并且此处的帖子信息已被优化, 没必要全部传输
        List<Post> postList = postDao.queryAllByLimit(userId, type, offset, limit, orderMode);
        if (postList == null || postList.size() == 0)
            return new ArrayList<>();

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

    /**
     * 查询用户的关注的笔记
     *
     * @param holderId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> queryFolloweeNotes(int holderId, int offset, int limit) {
        // 查出关注的人的Id
        String redisKey = RedisKeyUtil.getFolloweeKey(holderId, Constants.ENTITY_TYPE_USER);
        // 按分数从小到大返回set中的值, 也就是按时间从近到远
        Set<Integer> followeeIdSet = redisTemplate.opsForZSet()
                .reverseRange(redisKey, 0, Integer.MAX_VALUE);
        // 如果用户没有关注过任何人就会返回空
        if (followeeIdSet == null)
            return null;

        List<Post> list = postDao.queryFolloweePosts(offset, limit, followeeIdSet);

        // 把帖子相关信息封装起来传输
        List<Map<String, Object>> posts = new ArrayList<>();
        for (Post post : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            map.put("author", userService.querySimpleUserById(post.getUserId()));
            // 点赞数量
            map.put("likeCount", likeService.findEntityLikeCount(Constants.ENTITY_TYPE_POST, post.getId()));

            posts.add(map);
        }
        return posts;
    }

    /**
     * 根据问答状态查询问答
     *
     * @param QAState
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> queryQAByState(int QAState, int offset, int limit) {
        List<Post> postList = postDao.queryQAByState(QAState, offset, limit);
        if (postList == null || postList.size() == 0)
            return new ArrayList<>();

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
