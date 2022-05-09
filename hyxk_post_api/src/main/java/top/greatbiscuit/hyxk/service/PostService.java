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

    /**
     * 修改帖子状态
     *
     * @param postId
     * @param state  [0-正常 1-加精 2-删除]
     * @return
     */
    String updatePostState(int postId, int state);

    /**
     * 设置帖子置顶
     *
     * @param postId
     * @return
     */
    String setPostTop(int postId);

    /**
     * 得到帖子数量
     *
     * @param type -1则为所有
     * @return
     */
    long getPostCount(int type);

    /**
     * 查询帖子是否存在
     *
     * @param postId
     * @return
     */
    boolean exitsPost(int postId);

}
