package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.service.LikeService;

/**
 * 点赞服务生成者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/22 18:00
 */
@DubboService(version = "v1.0.0")
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    /**
     * 点赞[取消点赞也是调用该方法, 在此处进行判断]
     *
     * @param userId       当前用户
     * @param entityType   被点赞的实体类型
     * @param entityId     被点赞的实体ID
     * @param entityUserId 被点赞的实体的发布者
     * @return
     */
    @Override
    public String like(int userId, int entityType, int entityId, int entityUserId) {
        // 此处需要使用Redis的事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                // 开启事务
                operations.multi();

                if (isMember) {
                    // 已点赞，所以现在是取消
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                } else {
                    // 点赞
                    // 实体获赞则放在set里
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    // 统计用户获赞数量
                    redisTemplate.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
        return null;
    }

    /**
     * 查询某个实体获赞的总数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisService.getSetSize(redisKey);
    }

    /**
     * 用户是否对这一实体点赞
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public boolean userHasLike(int userId, int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisService.isMemberOfSet(redisKey, userId);
    }

    /**
     * 用户被点赞总数
     *
     * @param userId
     * @return
     */
    @Override
    public int findUserLikeCount(int userId) {
        String redisKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = redisService.getCacheObject(redisKey);
        // 防止用户没被点赞过
        return count == null ? 0 : count;
    }

}
