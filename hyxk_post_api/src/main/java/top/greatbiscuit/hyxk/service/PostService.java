package top.greatbiscuit.hyxk.service;

import top.greatbiscuit.hyxk.entity.Post;

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

}
