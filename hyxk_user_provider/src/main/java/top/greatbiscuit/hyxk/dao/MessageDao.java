package top.greatbiscuit.hyxk.dao;

import org.apache.ibatis.annotations.Mapper;
import top.greatbiscuit.hyxk.entity.Message;

import java.util.List;

/**
 * 消息表(Message)实体类
 *
 * @Author GreatBiscuit
 * @Date 2022-03-27 20:40:32
 */
@Mapper
public interface MessageDao {

    /**
     * 查询私信列表[返回每个私信最新的一条消息]
     *
     * @param userId
     * @return
     */
    List<Message> queryConversations(int userId);

    /**
     * 查询私信详情[返回私信的所有数据]
     *
     * @param conversationId
     * @return
     */
    List<Message> queryLetters(String conversationId);

    /**
     * 查询未读私信数量[conversationId为null时查询未读私信总数]
     *
     * @param userId
     * @param conversationId
     * @return
     */
    int queryLetterUnreadCount(int userId, String conversationId);

    /**
     * 新增消息[私信和系统通知都是消息]
     *
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 查询未读通知数
     *
     * @param userId
     * @param topic
     * @return
     */
    int queryNoticeUnreadCount(int userId, String topic);

    /**
     * 查询某一类型的通知详情
     *
     * @param userId
     * @param topic
     * @return
     */
    List<Message> queryNotices(int userId, String topic);

    /**
     * 修改状态
     *
     * @param ids
     * @param state
     * @return
     */
    int updateState(List<Integer> ids, int state);

    /**
     * 查询帖子的头图
     *
     * @param postId
     * @return
     */
    String queryPostHeaderImg(Integer postId);

    /**
     * 查询评论的内容
     *
     * @param commentId
     * @return
     */
    String queryCommentContent(Integer commentId);

    /**
     * 查询被评论的实体类型
     *
     * @param commentId
     * @return
     */
    Integer queryCommentEntityType(Integer commentId);

    /**
     * 查询被评论的实体ID
     *
     * @param commentId
     * @return
     */
    Integer queryCommentEntityId(Integer commentId);
}

