package top.greatbiscuit.hyxk.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import top.greatbiscuit.hyxk.entity.Comment;

import java.util.List;

/**
 * 评论表(Comment)表数据库访问层
 *
 * @author makejava
 * @since 2022-03-21 18:49:09
 */
@Mapper
public interface CommentDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param comment  查询条件
     * @param pageable 分页对象
     * @return 对象列表
     */
    List<Comment> queryAllByLimit(Comment comment, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param comment 查询条件
     * @return 总行数
     */
    long count(Comment comment);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int update(Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 查询出实体的评论
     *
     * @param entityType
     * @param entityId
     * @return
     */
    List<Comment> queryCommentsByEntity(Integer entityType, Integer entityId);

    /**
     * 查询对某个实体的评论总数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    int queryCountByEntity(Integer entityType, Integer entityId);

}

