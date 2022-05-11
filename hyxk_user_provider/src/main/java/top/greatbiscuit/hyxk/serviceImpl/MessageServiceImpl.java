package top.greatbiscuit.hyxk.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.dao.MessageDao;
import top.greatbiscuit.hyxk.dao.UserDao;
import top.greatbiscuit.hyxk.entity.Message;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.service.FollowService;
import top.greatbiscuit.hyxk.service.MessageService;

import java.util.*;

/**
 * 消息服务生产者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/28 23:42
 */
@DubboService(version = "v1.0.0")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserDao userDao;

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private FollowService followService;

    /**
     * 是否有未读消息
     *
     * @param userId
     * @return
     */
    @Override
    public boolean hasUnreadMessage(int userId) {
        // 判断是否有未读系统通知
        Map<String, Integer> noticeUnreadCount = getNoticeUnreadCount(userId);
        for (Integer value : noticeUnreadCount.values()) {
            if (value != 0) {
                return true;
            }
        }
        // 判断是否有未读私信
        if (messageDao.queryLetterUnreadCount(userId, null) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 得到每个通知在消息首页显示的信息
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Integer> getNoticeUnreadCount(int userId) {
        Map<String, Integer> noticeUnreadCount = new HashMap<>();
        // 点赞通知
        noticeUnreadCount.put("like", messageDao.queryNoticeUnreadCount(userId, Constants.TOPIC_LIKE));
        // 关注通知
        noticeUnreadCount.put("follow", messageDao.queryNoticeUnreadCount(userId, Constants.TOPIC_FOLLOW));
        // 评论通知
        noticeUnreadCount.put("comment", messageDao.queryNoticeUnreadCount(userId, Constants.TOPIC_COMMENT));
        // 收藏通知
        noticeUnreadCount.put("collect", messageDao.queryNoticeUnreadCount(userId, Constants.TOPIC_COLLECT));

        return noticeUnreadCount;
    }

    /**
     * 得到消息首页的私信列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<Map<String, Object>> getLetterHome(int userId) {
        List<Message> conversationList = messageDao.queryConversations(userId);
        // 如果没有就返回null
        if (conversationList == null) {
            return null;
        }
        // 包装私信列表
        List<Map<String, Object>> conversationListVo = new ArrayList<>();
        for (Message conversation : conversationList) {
            Map<String, Object> conversationVo = new HashMap<>();
            // 对方的Id
            int targetId = userId == conversation.getFromId() ?
                    conversation.getToId() : conversation.getFromId();
            conversationVo.put("targetUser", userDao.querySimpleUserById(targetId));
            conversationVo.put("lastLetter", conversation);
            conversationVo.put("unreadCount", messageDao.queryLetterUnreadCount(userId, conversation.getConversationId()));
            conversationListVo.add(conversationVo);
        }

        return conversationListVo;
    }

    /**
     * 得到私信详情
     *
     * @param conversationId
     * @return
     */
    @Override
    public Map<String, Object> getLetterDetail(int userId, String conversationId) {
        // 可以得到两个Id
        String[] idPair = conversationId.split("_");
        // 得到对方的Id
        int targetId = userId == Integer.parseInt(idPair[0]) ?
                Integer.parseInt(idPair[1]) : Integer.parseInt(idPair[0]);
        User target = userDao.querySimpleUserById(targetId);
        // 防止对方用户不存在[当前用户已登录, 肯定存在]
        if (target == null) {
            return null;
        }

        // 构造返回数据
        Map<String, Object> map = new HashMap<>();
        // 得到两个用户的基本信息
        map.put("target", target);
        map.put("holder", userDao.querySimpleUserById(userId));
        List<Message> letterList = messageDao.queryLetters(conversationId);
        if (letterList == null) {
            letterList = new ArrayList<>();
        }
        map.put("letterList", letterList);
        // 将私信设为已读
        readMessage(letterList, userId);
        return map;
    }

    /**
     * 将消息设为已读[在用户查看通知或私信详情时被调用]
     *
     * @param messageList
     */
    private void readMessage(List<Message> messageList, int holderUserId) {
        // 防止其为空
        if (messageList == null) {
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (Message message : messageList) {
            // 只改变toId为当前用户的消息的状态
            if (holderUserId == message.getToId() && message.getState() == 0) {
                ids.add(message.getId());
            }
        }
        // 为空则无需处理
        if (ids.isEmpty()) {
            return;
        }
        // 修改状态[1-已读]
        messageDao.updateState(ids, 1);
    }

    /**
     * 发送私信[发送系统通知在消息队列里实现]
     *
     * @param fromId
     * @param toId
     * @param content
     * @return
     */
    @Override
    public String sendLetter(int fromId, int toId, String content) {
        // 防止目标用户不存在
        if (userDao.querySimpleUserById(toId) == null) {
            return "目标用户不存在!";
        }
        Message letter = new Message();
        letter.setFromId(fromId);
        letter.setToId(toId);
        letter.setContent(content);
        // 未读
        letter.setState(0);
        letter.setCreateTime(new Date());
        // 处理会话ID
        if (fromId < toId) {
            letter.setConversationId(fromId + "_" + toId);
        } else {
            letter.setConversationId(toId + "_" + fromId);
        }
        // 添加进数据库
        messageDao.insertMessage(letter);
        return null;
    }

    /**
     * 获取通知列表[点赞、收藏、评论]
     *
     * @param holderId
     * @param type
     * @return
     */
    @Override
    public List<Map<String, Object>> getNoticeList(int holderId, String type) {
        List<Message> noticeList = messageDao.queryNotices(holderId, type);
        if (noticeList == null) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> noticeListVo = new ArrayList<>();
        for (Message notice : noticeList) {
            Map<String, Object> map = new HashMap<>();
            // 得到其他数据
            Map<String, Object> data = JSONObject.parseObject(notice.getContent(), HashMap.class);

            map.put("id", notice.getId());
            map.put("type", type);
            map.put("isUnread", notice.getState() != 0);

            Map<String, Object> targetEntity = new HashMap<>();
            // 先全部置空
            targetEntity.put("headerImg", null);
            targetEntity.put("comment", null);
            if (type.equals(Constants.TOPIC_COLLECT)) {
                // 收藏
                map.put("postId", data.get("entityId"));
                targetEntity.put("headerImg", messageDao.queryPostHeaderImg((Integer) data.get("entityId")));
            } else if (type.equals(Constants.TOPIC_LIKE)) {
                // 点赞
                map.put("postId", data.get("postId"));
                Integer likeType = (Integer) data.get("entityType");
                if (likeType == Constants.ENTITY_TYPE_POST) {
                    targetEntity.put("headerImg", messageDao.queryPostHeaderImg((Integer) data.get("entityId")));
                } else {
                    // 如果是对评论进行点赞就需要记录评论的内容
                    targetEntity.put("comment", messageDao.queryCommentContent((Integer) data.get("entityId")));
                }
            } else {
                // 评论或回复
                map.put("postId", data.get("postId"));

                Integer commentId = (Integer) data.get("commentId");

                if (messageDao.queryCommentEntityType(commentId) == Constants.ENTITY_TYPE_COMMENT) {
                    // 如果是对评论进行回复, 通知的类型就需要变为reply
                    map.put("type", "reply");
                    // 如果是回复评论
                    int byCommentId = messageDao.queryCommentEntityId(commentId);
                    targetEntity.put("comment", messageDao.queryCommentContent(byCommentId));
                } else {
                    // 如果是评论帖子
                    int byCommentPostId = messageDao.queryCommentEntityId(commentId);
                    targetEntity.put("headerImg", messageDao.queryPostHeaderImg(byCommentPostId));
                }
                map.put("content", messageDao.queryCommentContent(commentId));
            }
            map.put("targetEntity", targetEntity);
            map.put("time", notice.getCreateTime());

            // 处理from
            User user = userDao.querySimpleUserById((Integer) data.get("userId"));
            map.put("from", user);

            noticeListVo.add(map);
        }
        return noticeListVo;
    }

    /**
     * 获取关注通知列表
     *
     * @param holderId
     * @return
     */
    @Override
    public List<Map<String, Object>> getFollowNoticeList(int holderId) {
        List<Message> noticeList = messageDao.queryNotices(holderId, Constants.TOPIC_FOLLOW);
        if (noticeList == null) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> noticeListVo = new ArrayList<>();
        for (Message notice : noticeList) {
            Map<String, Object> map = new HashMap<>();
            // 得到其他数据
            Map<String, Object> data = JSONObject.parseObject(notice.getContent(), HashMap.class);
            int formId = (Integer) data.get("userId");
            map.put("from", userDao.querySimpleUserById(formId));
            map.put("id", notice.getId());
            map.put("isUnread", notice.getState() != 0);
            map.put("followed", followService.hasFollowed(holderId, Constants.ENTITY_TYPE_USER, formId));
            map.put("time", notice.getCreateTime());
            noticeListVo.add(map);
        }
        return noticeListVo;
    }
}
