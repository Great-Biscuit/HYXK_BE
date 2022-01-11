package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.UserService;

/**
 * @Author: GreatBiscuit
 * @Date: 2022/1/10 15:58
 */
@DubboService(version = "v1.0.0")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    @ShenyuDubboClient(path = "/select", desc = "按ID查询用户")
    public User queryById(Integer id) {
        System.out.println("\nhello2.....................................\n");
        return userDao.queryById(id);
    }

    @ShenyuDubboClient(path = "/hello", desc = "测试")
    public String hello() {
        return "hello!";
    }

}
