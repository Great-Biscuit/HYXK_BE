package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.Post;

import java.util.List;

/**
 * 搜索服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/26 18:55
 */
public interface SearchService {

    /**
     * 保存帖子
     *
     * @param postId
     */
    void savePost(int postId);

    /**
     * 删除帖子
     *
     * @param postId
     */
    void deletePost(int postId);

    /**
     * 查询
     *
     * @param text
     * @param current
     * @param limit
     * @param type    帖子类型[为空时则查询所有帖子]
     * @return
     */
    List<Post> searchPostList(String text, int current, int limit, Integer type);

}
