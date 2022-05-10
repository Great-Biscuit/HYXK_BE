package top.greatbiscuit.hyxk.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.greatbiscuit.hyxk.entity.Post;

import java.util.List;
import java.util.Set;

/**
 * 帖子表(Post)表数据库访问层
 *
 * @author makejava
 * @since 2022-03-19 17:56:21
 */
@Mapper
public interface PostDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Post queryById(Integer id);

    /**
     * 查询指定行数据[用户id不为0就查询指定用户, 否则查询所有--先按top排序保证顶置在最前]
     * [orderMode为1则按分数再按时间排序 为0则按时间排序]
     * [只针对首页帖子列表查询出了必要的数据]
     *
     * @param userId
     * @param type
     * @param offset
     * @param limit
     * @param orderMode
     * @return
     */
    List<Post> queryAllByLimit(int userId, int type, int offset, int limit, int orderMode);

    /**
     * 统计总行数
     *
     * @param post 查询条件
     * @return 总行数
     */
    long count(Post post);

    /**
     * 新增数据
     *
     * @param post 实例对象
     * @return 影响行数
     */
    int insert(Post post);

    /**
     * 修改数据
     *
     * @param post 实例对象
     * @return 影响行数
     */
    int update(Post post);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 通过Id查询Markdown内容
     *
     * @param id
     * @return
     */
    String queryMarkdownById(Integer id);

    /**
     * 根据Id修改Html内容
     *
     * @param id
     * @param htmlContent
     * @return
     */
    int updateHtmlById(Integer id, String htmlContent);

    /**
     * 更新评论总数
     *
     * @param id
     * @param comments
     * @return
     */
    int updateCommentCount(Integer id, int comments);

    /**
     * 更新帖子分数
     *
     * @param id
     * @param score
     * @return
     */
    int updateScore(Integer id, double score);

    /**
     * 查询用户的关注的文章列表
     *
     * @param offset
     * @param limit
     * @param ids    关注的Id
     * @return
     */
    List<Post> queryFolloweePosts(int offset, int limit, @Param("ids") Set<Integer> ids);

    /**
     * 根据问答状态查询问答
     *
     * @param state
     * @param offset
     * @param limit
     * @return
     */
    List<Post> queryQAByState(int state, int offset, int limit);
}

