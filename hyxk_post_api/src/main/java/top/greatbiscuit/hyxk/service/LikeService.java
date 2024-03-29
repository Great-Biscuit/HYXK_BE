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
     * @param userId
     * @param entityType
     * @param entityId
     * @param entityUserId
     * @param postId       让前端传入, 否则数据库交互过多
     * @return
     */
    String like(int userId, int entityType, int entityId, int entityUserId, int postId);

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

}
