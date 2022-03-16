package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.hyxk.service.PostService;

/**
 * 帖子相关服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/9 17:16
 */
@DubboService(version = "v1.0.0")
public class PostServiceImpl implements PostService {

    @Autowired
    private RedisService redisService;

    @Autowired
    public RedisTemplate redisTemplate;

    @Override
    public int testRedis(int temp) {
        String redisKey = "test:count";

        redisService.setCacheObject(redisKey, temp);
        System.out.println("存在:" + redisService.hasKey(redisKey));

        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));

        return redisService.getCacheObject(redisKey);
    }
}
