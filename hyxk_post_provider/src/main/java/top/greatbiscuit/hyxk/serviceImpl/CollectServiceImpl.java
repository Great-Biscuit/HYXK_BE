package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.service.CollectService;

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

}
