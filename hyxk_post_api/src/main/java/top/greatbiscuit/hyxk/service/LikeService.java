package top.greatbiscuit.hyxk.service;

/**
 * 点赞服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/22 18:00
 */
public interface LikeService {

    /**
     * 点赞
     *
     * @param userId       当前用户
     * @param entityType   被点赞的实体类型
     * @param entityId     被点赞的实体ID
     * @param entityUserId 被点赞的实体的发布者
     * @return
     */
    String like(int userId, int entityType, int entityId, int entityUserId);

    /**
     * 查询某个实体获赞的总数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 用户是否对这一实体点赞
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean userHasLike(int userId, int entityType, int entityId);

    /**
     * 用户被点赞总数
     *
     * @param userId
     * @return
     */
    int findUserLikeCount(int userId);
}
