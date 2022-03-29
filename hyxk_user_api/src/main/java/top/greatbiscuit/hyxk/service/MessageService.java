package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.Message;

import java.util.List;
import java.util.Map;

/**
 * 消息服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/28 23:42
 */
public interface MessageService {

    /**
     * 得到每个通知在消息首页显示的信息[即有几个未读]
     *
     * @param userId
     * @return
     */
    Map<String, Integer> getNoticeUnreadCount(int userId);

    /**
     * 得到消息首页的私信列表
     *
     * @param userId
     * @return
     */
    List<Map<String, Object>> getLetterHome(int userId);

    /**
     * 得到通知详情
     *
     * @param userId
     * @param topic
     * @return
     */
    List<Message> getNoticeDetail(int userId, String topic);

    /**
     * 得到私信详情
     *
     * @param userId
     * @param conversationId
     * @return
     */
    Map<String, Object> getLetterDetail(int userId, String conversationId);

    /**
     * 发送私信[发送系统通知在消息队列里实现]
     *
     * @param fromId
     * @param toId
     * @param content
     * @return
     */
    String sendLetter(int fromId, int toId, String content);

}
