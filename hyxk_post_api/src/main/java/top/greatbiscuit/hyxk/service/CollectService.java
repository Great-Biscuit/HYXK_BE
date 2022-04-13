package top.greatbiscuit.hyxk.service;

import java.util.List;
import java.util.Map;

/**
 * 收藏服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/2 18:04
 */
public interface CollectService {

    /**
     * 帖子被收藏数量
     *
     * @param postId
     * @return
     */
    long findPostCollectCount(int postId);

    /**
     * 查询用户收藏的帖子列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> getCollectedPostList(int userId, int offset, int limit);

}
