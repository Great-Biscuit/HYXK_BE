package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.Post;

import java.util.Map;

/**
 * 帖子(Post)表服务接口
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/9 17:14
 */
public interface PostService {

    /**
     * 新增帖子
     *
     * @param post 帖子对象
     * @return
     */
    String insertPost(Post post);

    /**
     * 删除帖子
     *
     * @param userId
     * @param postId
     * @return
     */
    String deletePost(int userId, int postId);

    /**
     * 通过ID查询单条数据
     *
     * @param id
     * @param holderUserId
     * @return
     */
    Map<String, Object> queryPostDetailById(Integer id, Integer holderUserId);

}
