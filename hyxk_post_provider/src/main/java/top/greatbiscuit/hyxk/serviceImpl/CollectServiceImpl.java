package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.CollectService;
import top.greatbiscuit.hyxk.service.UserService;

import java.util.*;

/**
 * 帖子收藏服务生产者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/2 18:05
 */
@DubboService(version = "v1.0.0")
public class CollectServiceImpl implements CollectService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PostDao postDao;

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private UserService userService;

    /**
     * 帖子被收藏数量
     *
     * @param postId
     * @return
     */
    @Override
    public long findPostCollectCount(int postId) {
        String redisKey = RedisKeyUtil.getFollowerKey(Constants.ENTITY_TYPE_POST, postId);
        return redisService.getZSetSize(redisKey);
    }


    /**
     * 查询用户收藏的帖子列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> getCollectedPostList(int userId, int offset, int limit) {
        String redisKey = RedisKeyUtil.getFolloweeKey(userId, Constants.ENTITY_TYPE_POST);
        // 按分数从小到大返回set中的值, 也就是按时间从近到远
        Set<Integer> followeeIdSet = redisTemplate.opsForZSet()
                .reverseRange(redisKey, offset, offset + limit - 1);
        // 如果用户没有关注过任何人就会返回空
        if (followeeIdSet == null)
            return null;

        // 用户List对上述结果进行加工
        List<Map<String, Object>> collectedPostList = new ArrayList<>();
        for (Integer postId : followeeIdSet) {
            Map<String, Object> map = new HashMap<>();
            Post post = postDao.queryById(postId);
            // 防止帖子被删除而不存在
            if (post == null) {
                // 从zset把它删除, 因为已经不存在了
                redisTemplate.opsForZSet().remove(redisKey, postId);
                continue;
            }
            // 帖子
            map.put("post", post);
            // 作者
            map.put("author", userService.querySimpleUserById(post.getUserId()));
            // 点赞数量
            String likeKey = RedisKeyUtil.getEntityLikeKey(Constants.ENTITY_TYPE_POST, postId);
            map.put("likeCount", redisService.getSetSize(likeKey));
            collectedPostList.add(map);
        }
        return collectedPostList;
    }

}
