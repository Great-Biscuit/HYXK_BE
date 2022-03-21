package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.Comment;

import java.util.List;

/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2022-03-21 18:45:29
 */
public interface CommentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment queryById(Integer id);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    int insert(Comment comment);

    /**
     * 查询出实体的评论
     *
     * @param entityTypePost
     * @param id
     * @return
     */
    List<Comment> findCommentsByEntity(Integer entityTypePost, Integer id);
}
