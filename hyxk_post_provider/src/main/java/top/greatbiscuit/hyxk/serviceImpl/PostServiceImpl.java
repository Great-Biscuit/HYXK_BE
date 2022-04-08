package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.CommentDao;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Comment;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.event.Event;
import top.greatbiscuit.hyxk.event.EventProducer;
import top.greatbiscuit.hyxk.service.*;

import java.util.*;

/**
 * 帖子相关服务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/9 17:16
 */
@DubboService(version = "v1.0.0")
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisService redisService;

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    @DubboReference(version = "v1.0.0")
    private LikeService likeService;

    @DubboReference(version = "v1.0.0")
    private CollectService collectService;

    @DubboReference(version = "v1.0.0")
    private FollowService followService;

    /**
     * 新增帖子
     *
     * @param post 帖子对象
     * @return
     */
    @Override
    public String insertPost(Post post) {
        if (post == null) {
            return "未获取到帖子信息!";
        }

        // 如果是不是普通用户或者注销用户就是官方
        int userType = userService.queryUserType(post.getUserId());
        post.setOfficial((userType != 0 && userType != Constants.USER_TYPE_DESTROY) ? 1 : 0);
        // 将标题进行转义
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setCreateTime(new Date());
        // 暂时将Html中的内容设置为特定内容
        post.setHtmlContent("系统处理中, 请刷新页面...");
        postDao.insert(post);

        // Markdown转Html耗时太长, 放到消息队列里去
        // 同时将数据加入es
        Event event = new Event()
                .setTopic(Constants.TOPIC_PUBLISH)
                .setUserId(post.getUserId())
                .setEntityType(Constants.ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        // 发布事件
        eventProducer.fireEvent(event);

        // 将帖子加入需要更新分数的帖子编号Set中, 等待自动任务更新帖子分数
        String flushScoreKey = RedisKeyUtil.getPostScoreKey();
        redisService.addCacheSet(flushScoreKey, post.getId());

        return null;
    }

    /**
     * 删除帖子
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    public String deletePost(int userId, int postId) {
        // 该请求数量较少, 可以直接把帖子全部查出来
        Post post = postDao.queryById(postId);
        if (post == null) {
            return "数据不存在!";
        }
        // 防止删除别人的帖子
        if (post.getUserId() != userId) {
            return "权限不足!";
        }
        // 删除帖子[修改状态为2]
        post.setState(2);
        postDao.update(post);
        // 触发删帖事件, 从es中删除数据
        Event event = new Event()
                .setTopic(Constants.TOPIC_DELETE)
                .setUserId(userId)
                .setEntityType(Constants.ENTITY_TYPE_POST)
                .setEntityId(postId);
        eventProducer.fireEvent(event);
        return null;
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Map<String, Object> queryPostDetailById(Integer id, Integer holderUserId) {
        Map<String, Object> postDetail = new HashMap<>();
        // 先查询帖子自身的数据
        Post post = postDao.queryById(id);
        // 帖子不存在或者被删除
        if (post == null || post.getState() == 2) {
            return null;
        }
        postDetail.put("post", post);

        // 查询帖子作者的数据
        User postAuthor = userService.querySimpleUserById(post.getUserId());
        postDetail.put("author", postAuthor);

        // 帖子获得的赞
        long likeCount = likeService.findEntityLikeCount(Constants.ENTITY_TYPE_POST, id);
        postDetail.put("likeCount", likeCount);

        // 当前用户是否对帖子点赞
        boolean hasLike = holderUserId != null
                && likeService.userHasLike(holderUserId, Constants.ENTITY_TYPE_POST, id);
        postDetail.put("hasLike", hasLike);

        // 帖子收藏数
        long collectCount = collectService.findPostCollectCount(id);
        postDetail.put("collectCount", collectCount);

        // 当前用户是否收藏
        boolean hasCollect = holderUserId != null
                && followService.hasFollowed(holderUserId, Constants.ENTITY_TYPE_POST, id);
        postDetail.put("hasCollect", hasCollect);

        /*
         * 评论: 对帖子的评论
         * 回复: 对评论的评论
         */
        // 查询帖子的所有评论
        List<Comment> commentList = commentDao.queryCommentsByEntity(Constants.ENTITY_TYPE_POST, post.getId());
        // 对每个评论进行处理[放入用户数据, 评论的回复等数据]
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 一个评论的VO
                Map<String, Object> commentVo = new HashMap<>();
                // 往VO里添加评论
                commentVo.put("commentText", comment.getContent());
                // 评论的作者
                commentVo.put("user", userService.querySimpleUserById(comment.getUserId()));

                // 评论的赞
                likeCount = likeService.findEntityLikeCount(Constants.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);

                // 当前用户是否点赞
                hasLike = holderUserId != null
                        && userService.queryUserById(holderUserId) != null
                        && likeService.userHasLike(holderUserId, Constants.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("hasLike", hasLike);

                // 回复列表
                List<Comment> replyList = commentDao.queryCommentsByEntity(Constants.ENTITY_TYPE_COMMENT, comment.getId());
                // 回复的VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("replyText", reply.getContent());
                        // 作者
                        replyVo.put("user", userService.querySimpleUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.querySimpleUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // 回复的赞
                        likeCount = likeService.findEntityLikeCount(Constants.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);

                        // 当前用户是否对回复点赞
                        hasLike = holderUserId != null
                                && userService.queryUserById(holderUserId) != null
                                && likeService.userHasLike(holderUserId, Constants.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("hasLike", hasLike);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replyVoList", replyVoList);

                commentVoList.add(commentVo);
            }
        }

        postDetail.put("commentVoList", commentVoList);

        return postDetail;
    }

    // TODO: 还有帖子的置顶和加精

}
