package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.UserService;

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

    @Override
    public User queryById(Integer id) {
        return userDao.queryById(id);
    }

    @Override
    public User queryByUsername(String username) {
        return userDao.queryByUsername(username);
    }

}
