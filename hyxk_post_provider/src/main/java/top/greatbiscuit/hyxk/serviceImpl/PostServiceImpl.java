package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.event.Event;
import top.greatbiscuit.hyxk.event.EventProducer;
import top.greatbiscuit.hyxk.service.PostService;

import java.util.Date;

/**
 * 帖子相关服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/9 17:16
 */
@DubboService(version = "v1.0.0")
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 新增帖子
     *
     * @param post 帖子对象
     * @return
     */
    @Override
    public String insertPost(Post post) {
        if (post == null) {
            return "未获取到帖子信息!";
        }

        post.setCreateTime(new Date());
        // 暂时将Html中的内容设置为特定内容
        post.setHtmlContent("系统处理中, 请刷新页面...");
        postDao.insert(post);

        // Markdown转Html耗时太长, 放到消息队列里去
        Event event = new Event()
                .setTopic(Constants.TOPIC_PUBLISH)
                .setUserId(post.getUserId())
                .setEntityType(Constants.ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        // 发布事件
        eventProducer.fireEvent(event);
        return null;
    }

}
