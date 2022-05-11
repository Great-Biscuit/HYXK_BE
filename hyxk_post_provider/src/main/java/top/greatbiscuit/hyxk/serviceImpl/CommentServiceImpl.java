package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.CommentDao;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Comment;
import top.greatbiscuit.hyxk.entity.Post;
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

    @Autowired
    private RedisService redisService;

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

            // 触发发帖事件[需要修改帖子信息, 以及插入es]
            Event publishEvent = new Event()
                    .setTopic(Constants.TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(Constants.ENTITY_TYPE_POST)
                    .setEntityId(postId);
            eventProducer.fireEvent(publishEvent);

            // 如果是对帖子评论, 就需要更新帖子分数
            // 将帖子加入需要更新分数的帖子编号Set中, 等待自动任务更新帖子分数
            String flushScoreKey = RedisKeyUtil.getPostScoreKey();
            redisService.addCacheSet(flushScoreKey, postId);

        }

        // 触发系统通知, 使系统给用户发送消息
        Event commentEvent = new Event()
                .setTopic(Constants.TOPIC_COMMENT)
                .setUserId(comment.getUserId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", postId)
                .setData("commentId", comment.getId());
        // 找出该消息是发给谁的
        if (comment.getEntityType() == Constants.ENTITY_TYPE_POST) {
            Post target = postDao.queryById(comment.getEntityId());
            commentEvent.setEntityUserId(target.getUserId());
        } else {
            Comment target = commentDao.queryById(comment.getEntityId());
            commentEvent.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(commentEvent);

        return null;
    }

    /**
     * 删除评论[改变评论状态]
     *
     * @param commentId
     * @param userId
     * @return
     */
    @Override
    public String deleteComment(int commentId, int userId) {
        Comment comment = commentDao.queryById(commentId);
        if (comment == null) {
            return "评论不存在!";
        }
        // 防止评论被他人删除
        if (userId != comment.getUserId()) {
            return "无权限!";
        }
        // 使评论失效
        comment.setState(1);
        // 修改数据库
        commentDao.update(comment);
        // 如果是对帖子的评论就要更新帖子的评论数和帖子分数
        if (comment.getEntityType() == Constants.ENTITY_TYPE_POST) {
            // 更新对帖子的评论数[回复是对评论的评论, 不做为对帖子的评论数]
            int count = commentDao.queryCountByEntity(Constants.ENTITY_TYPE_POST, comment.getEntityId());
            postDao.updateCommentCount(comment.getEntityId(), count);

            // 将帖子加入需要更新分数的帖子编号Set中, 等待自动任务更新帖子分数
            String flushScoreKey = RedisKeyUtil.getPostScoreKey();
            redisService.addCacheSet(flushScoreKey, comment.getEntityId());
        }
        return null;
    }

}
