package top.greatbiscuit.hyxk.dao;

import org.apache.ibatis.annotations.Mapper;
import top.greatbiscuit.hyxk.entity.User;

import java.util.List;

/**
 * 用户表(User)表数据库访问层
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/10 16:01
 */
@Mapper
public interface UserDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Integer id);

    /**
     * 通过username查询单条数据
     *
     * @param username 用户名
     * @return 实例对象
     */
    User queryByUsername(String username);

    /**
     * 统计总行数
     *
     * @param user 查询条件
     * @return 总行数
     */
    long count(User user);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 影响行数
     */
    int update(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 通过邮箱查询数据
     *
     * @param email
     * @return
     */
    User queryByEmail(String email);

    /**
     * 查询出简略的用户信息[id nickname headerUrl type]
     *
     * @param id
     * @return
     */
    User querySimpleUserById(Integer id);

    /**
     * 查询用户列表
     *
     * @param offset
     * @param limit
     * @return
     */
    List<User> queryUserListByLimit(int offset, int limit);
}
