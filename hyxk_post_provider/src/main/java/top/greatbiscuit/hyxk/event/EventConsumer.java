package top.greatbiscuit.hyxk.event;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.utils.MarkdownUtil;

/**
 * 消费者
 */
@Component
public class EventConsumer {

    @Autowired
    private PostDao postDao;

    //消费发帖事件
    @KafkaListener(topics = {Constants.TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        // 消息内容为空
        if (record == null || record.value() == null) {
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 消息格式错误
        if (event == null) {
            return;
        }

        String markdownContent = postDao.queryMarkdownById(event.getEntityId());
        // 防止用户在此期间删帖
        if (markdownContent != null) {
            // Markdown转为Html
            String htmlContent = MarkdownUtil.markdown2Html(markdownContent);
            postDao.updateHtmlById(event.getEntityId(), htmlContent);
        }


    }

}
