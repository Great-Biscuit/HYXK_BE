package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.CommentDao;
import top.greatbiscuit.hyxk.entity.Comment;
import top.greatbiscuit.hyxk.service.CommentService;

import java.util.List;

/**
 * 评论服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/21 20:02
 */
@DubboService(version = "v1.0.0")
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Comment queryById(Integer id) {
        return commentDao.queryById(id);
    }

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public int insert(Comment comment) {
        return commentDao.insert(comment);
    }

    /**
     * 查询出实体的评论
     *
     * @param entityTypePost
     * @param id
     * @return
     */
    @Override
    public List<Comment> findCommentsByEntity(Integer entityTypePost, Integer id) {
        return commentDao.queryCommentsByEntity(entityTypePost, id);
    }
}
