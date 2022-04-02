package top.greatbiscuit.hyxk.service;

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

}
