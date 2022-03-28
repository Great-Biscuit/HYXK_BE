package top.greatbiscuit.hyxk.dao;

import top.greatbiscuit.hyxk.entity.Message;

/**
 * 消息表(Message)实体类
 *
 * @Author GreatBiscuit
 * @Date 2022-03-27 20:40:32
 */
public interface MessageDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Message queryById(Integer id);

}

