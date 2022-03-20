package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.PostService;
import top.greatbiscuit.hyxk.utils.MarkdownUtil;

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
        post.setHtmlContent(MarkdownUtil.markdown2Html(post.getMarkdownContent()));
        postDao.insert(post);
        return null;
    }

}
