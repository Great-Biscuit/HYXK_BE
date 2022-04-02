package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.UserService;
import top.greatbiscuit.hyxk.util.PasswordUtil;

import java.util.HashMap;
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
        user.setPassword(newPassword);
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
        User user = getCache(userId);//看Redis里有没有
        if (user == null) {
            user = initCache(userId);
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
        if (user == null) {
            user = initCache(userId);
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

    //使用Redis优化
    //1.优先从缓存里查
    private User getCache(int userId) {
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
