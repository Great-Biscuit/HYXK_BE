package top.greatbiscuit.hyxk.service;

import java.util.List;
import java.util.Map;

/**
 * 公开服务[直接通过网关调用DUBBO服务, 跳过consumer检验操作]
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/19 19:46
 */
public interface PostPublicService {

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
    List<Map<String, Object>> queryAllByLimit(int userId, int type, int offset, int limit, int orderMode);

    /**
     * 查询用户的关注的笔记
     *
     * @param holderId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> queryFolloweeNotes(int holderId, int offset, int limit);

    /**
     * 根据问答状态查询问答
     *
     * @param QAState
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> queryQAByState(int QAState, int offset, int limit);

}
