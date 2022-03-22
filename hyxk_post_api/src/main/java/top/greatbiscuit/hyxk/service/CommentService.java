package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.Comment;

/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2022-03-21 18:45:29
 */
public interface CommentService {

    /**
     * 新增数据
     *
     * @param comment
     * @return
     */
    String addComment(Comment comment);

    /**
     * 删除评论[改变评论状态]
     *
     * @param commentId
     * @param userId
     * @return
     */
    String deleteComment(int commentId, int userId);
}
