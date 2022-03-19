package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.PostPublicService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公开服务[直接通过网关调用DUBBO服务, 跳过consumer检验操作]
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/19 19:48
 */
@DubboService(version = "v1.0.0")
public class PostPublicServiceImpl implements PostPublicService {

    @Autowired
    private PostDao postDao;

    /**
     * 查询指定行数据[用户id不为0就查询指定用户, 否则查询所有--先按top排序保证顶置在最前]
     * [orderMode为1则按分数再按时间排序 为0则按时间排序]
     *
     * @param userId
     * @param type
     * @param offset
     * @param limit
     * @param orderMode
     * @return
     */
    @Override
    @ShenyuDubboClient(path = "/queryAll", desc = "查询帖子列表")
    public List<Map<String, Object>> queryAllByLimit(int userId, int type, int offset, int limit, int orderMode) {
        List<Post> postList = postDao.queryAllByLimit(userId, type, offset, limit, orderMode);
        if (postList == null || postList.size() == 0)
            return null;

        // 把帖子相关信息封装起来传输
        List<Map<String, Object>> posts = new ArrayList<>();
        for (Post post : postList) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            // TODO: 页面还需要别的信息, 如点赞数量、用户头像及名称. 并且此处的帖子信息可以优化, 没必要全部传输
            map.put("likeCount", 6);
            posts.add(map);
        }
        return posts;
    }

}
