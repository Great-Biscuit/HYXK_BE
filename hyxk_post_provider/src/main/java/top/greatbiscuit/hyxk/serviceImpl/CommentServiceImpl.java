package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.dao.CommentDao;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Comment;
import top.greatbiscuit.hyxk.event.Event;
import top.greatbiscuit.hyxk.event.EventProducer;
import top.greatbiscuit.hyxk.service.CommentService;

import java.util.Date;

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

    @Autowired
    private PostDao postDao;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 新增评论
     *
     * @param comment
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String addComment(Comment comment) {
        if (comment == null) {
            return "评论出错!";
        }
        comment.setState(0);
        comment.setCreateTime(new Date());

        int postId = comment.getEntityId();
        // 如果当前是对评论的回复，则查询出评论所在的帖子ID
        if (comment.getEntityType() == Constants.ENTITY_TYPE_COMMENT) {
            Comment fatherComment = commentDao.queryById(comment.getEntityId());
            postId = fatherComment.getEntityId();
        }

        // 插入数据库
        commentDao.insert(comment);

        // 如果的对帖子进行评论
        if (comment.getEntityType() == Constants.ENTITY_TYPE_POST) {
            // 更新对帖子的评论数[回复是对评论的评论, 不做为对帖子的评论数]
            int count = commentDao.queryCountByEntity(Constants.ENTITY_TYPE_POST, comment.getEntityId());
            postDao.updateCommentCount(postId, count);

            // 触发发帖事件
            Event event = new Event()
                    .setTopic(Constants.TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(Constants.ENTITY_TYPE_POST)
                    .setEntityId(postId);
            eventProducer.fireEvent(event);
        }

        // TODO: 触发系统通知, 使系统给用户发送消息

        return null;
    }

}
