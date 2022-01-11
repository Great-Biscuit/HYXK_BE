package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.User;

/**
 * 用户表(User)表服务接口
 *
 * @Author GreatBiscuit
 * @Date 2022-01-10 15:30:25
 */
public interface UserService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Integer id);

    String hello();

}
