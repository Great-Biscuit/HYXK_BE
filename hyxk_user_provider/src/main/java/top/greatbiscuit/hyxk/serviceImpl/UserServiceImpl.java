package top.greatbiscuit.hyxk.serviceImpl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.event.Event;
import top.greatbiscuit.hyxk.event.EventProducer;
import top.greatbiscuit.hyxk.service.FollowService;
import top.greatbiscuit.hyxk.service.UserService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户相关服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/10 15:58
 */
@DubboService(version = "v1.0.0")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private FollowService followService;

    private int maxSize = 100;   // 最多存100个数据

    private int expireSeconds = 60 * 60;    // 60分钟过期

    // 用户caffeine的缓存
    private LoadingCache<Integer, User> userCache;

    @Autowired
    private EventProducer eventProducer;

    @PostConstruct
    public void init() {
        //初始化用户缓存
        userCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, User>() {
                    //这里是往缓存里加数据
                    @Override
                    public @Nullable User load(@NonNull Integer key) throws Exception {
                        // 没有数据就先看redis里有没有
                        User user = getRedisCache(key);
                        if (user != null) {
                            // 查到了就返回
                            return user;
                        }
                        // 没查到就初始化数据
                        user = initCache(key);
                        if (user != null) {
                            return user;
                        }
                        // 实在没有就返回一个什么都没有的用户
                        user = new User();
                        return user;
                    }
                });
    }

    /**
     * 修改密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public Map updatePassword(Integer userId, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        User user = userDao.queryById(userId);

        //将提供的密码进行相同方式的加密
        oldPassword = PasswordUtil.md5(oldPassword + user.getSalt());

        //验证密码
        if (!oldPassword.equals(user.getPassword())) {
            map.put("passwordMsg", "密码错误!");
            return map;
        }

        //旧密码正确, 则修改密码
        user.setPassword(PasswordUtil.md5(newPassword + user.getSalt()));
        userDao.update(user);

        return map;
    }

    /**
     * 修改用户信息
     *
     * @param user
     */
    @Override
    public String updateUser(User user) {
        User u = userDao.queryById(user.getId());
        if (u == null) {
            return "用户不存在!";
        }
        // 只有部分信息可以修改
        u.setHeaderUrl(user.getHeaderUrl());
        u.setNickname(user.getNickname());
        u.setGender(user.getGender());
        u.setSignature(user.getSignature());

        userDao.update(u);
        // 修改用户信息后要清除缓存
        clearCache(user.getId());
        userCache.refresh(user.getId());
        return null;
    }

    /**
     * 修改所有用户信息[用于后台管理]
     *
     * @param user
     */
    @Override
    public String updateUserForAdmin(User user) {
        userDao.update(user);
        // 修改用户信息后要清除缓存
        clearCache(user.getId());
        userCache.refresh(user.getId());
        return null;
    }


    /**
     * 根据ID查询用户全部信息
     *
     * @param userId
     * @return
     */
    @Override
    public User queryUserById(Integer userId) {
        return userDao.queryById(userId);
    }

    /**
     * 根据ID查询出用户类型
     *
     * @param userId
     * @return
     */
    @Override
    public Integer queryUserType(Integer userId) {
        User user = getCache(userId);//看缓存里有没有
        if (user == null || user.getId() == null) {
            // 没查到就当作普通用户
            return 0;
        }
        return user.getType();
    }

    /**
     * 查询出简略的用户信息[id nickname headerUrl type]
     *
     * @param userId
     * @return
     */
    @Override
    public User querySimpleUserById(Integer userId) {
        User user = getCache(userId);//看Redis里有没有
        if (user == null || user.getId() == null) {
            // 没查到就返回空
            return null;
        }
        return user;
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

    /**
     * 查询用户列表
     *
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<User> getUserListByLimit(int offset, int limit) {
        return userDao.queryUserListByLimit(offset, limit);
    }

    /**
     * 用户是否存在
     *
     * @param userId
     * @return
     */
    @Override
    public boolean exitsUser(int userId) {
        User user = new User();
        user.setId(userId);
        return userDao.count(user) > 0;
    }

    /**
     * 注销账号
     *
     * @param holderId
     */
    @Override
    public void invalidUser(int holderId) {
        User user = userDao.queryById(holderId);
        if (user == null || user.getType() == Constants.USER_TYPE_DESTROY) {
            // 用户已经不存在或注销
            return;
        }
        // 发邮件
        String text = "【皓月星空站】亲爱的用户" +
                user.getNickname() +
                "(" +
                user.getUsername() +
                "), 您正在注销账号. <br/>感谢您对皓月星空站的支持, 让皓月星空站拥有您的印记, 愿追逐梦想的你梦想成真!" +
                "<br/>如果您在不久的将来想重回皓月星空站, 请回复此邮件进行申请, 皓月星空站永远欢迎您!" +
                "<br/><br/><br/>" +
                "<div style=\"font-size:60%; color:#b1b3b8\">该邮件由系统自动发出。<br/>" +
                "若您未进行相关操作, 请忽略本邮件, 对您造成打扰, 非常抱歉!</div>";

        // 发布事件发送邮件
        Event event = new Event()
                .setTopic(Constants.TOPIC_SEND_MAIL)
                .setData("to", user.getEmail())
                .setData("subject", "注销账号")
                .setData("content", text);
        // 发布
        eventProducer.fireEvent(event);

        user.setType(Constants.USER_TYPE_DESTROY);

        userDao.update(user);
    }

    /**
     * 搜索用户
     *
     * @param holderId 当前用户不存在则为null
     * @param key
     * @return
     */
    @Override
    public List<Map<String, Object>> searchByNickname(Integer holderId, String key) {
        List<User> userList = userDao.searchByNickname("%" + key + "%");
        if (userList == null) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : userList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("nickname", user.getNickname());
            map.put("avatar", user.getHeaderUrl());
            map.put("sex", user.getGender());
            map.put("description", user.getSignature());
            map.put("hasFollowed", holderId != null && followService.hasFollowed(holderId, Constants.ENTITY_TYPE_USER, user.getId()));
            result.add(map);
        }
        return result;
    }

    //1.优先从缓存里查
    private User getCache(int userId) {
        return userCache.get(userId);
    }

    private User getRedisCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return redisService.getCacheObject(redisKey);
    }

    //2.缓存没有就初始化
    private User initCache(int userId) {
        User user = userDao.querySimpleUserById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisService.setCacheObject(redisKey, user, 1L, TimeUnit.HOURS);//1小时有效
        return user;
    }

    //3.修改后清除缓存
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisService.deleteObject(redisKey);
    }

}
