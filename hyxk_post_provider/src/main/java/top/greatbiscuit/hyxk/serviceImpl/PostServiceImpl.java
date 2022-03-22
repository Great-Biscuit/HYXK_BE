package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.dao.CommentDao;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Comment;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.entity.User;
import top.greatbiscuit.hyxk.event.Event;
import top.greatbiscuit.hyxk.event.EventProducer;
import top.greatbiscuit.hyxk.service.LikeService;
import top.greatbiscuit.hyxk.service.PostService;
import top.greatbiscuit.hyxk.service.UserService;

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

    @DubboReference(version = "v1.0.0")
    private UserService userService;

    @DubboReference(version = "v1.0.0")
    private LikeService likeService;

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

        post.setCreateTime(new Date());
        // 暂时将Html中的内容设置为特定内容
        post.setHtmlContent("系统处理中, 请刷新页面...");
        postDao.insert(post);

        // Markdown转Html耗时太长, 放到消息队列里去
        Event event = new Event()
                .setTopic(Constants.TOPIC_PUBLISH)
                .setUserId(post.getUserId())
                .setEntityType(Constants.ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        // 发布事件
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
        postDetail.put("post", post);

        // 查询帖子作者的数据
        User postAuthor = userService.querySimpleUserById(post.getUserId());
        postDetail.put("author", postAuthor);

        // 帖子获得的赞
        long likeCount = likeService.findEntityLikeCount(Constants.ENTITY_TYPE_POST, id);
        postDetail.put("likeCount", likeCount);

        // 当前用户是否对帖子点赞
        boolean hasLike = (holderUserId == null || userService.queryUserById(holderUserId) == null) ? false :
                likeService.userHasLike(holderUserId, Constants.ENTITY_TYPE_POST, id);
        postDetail.put("hasLike", hasLike);

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
                commentVo.put("comment", comment);
                // 评论的作者
                commentVo.put("user", userService.querySimpleUserById(comment.getUserId()));

                // 评论的赞
                likeCount = likeService.findEntityLikeCount(Constants.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);

                // 当前用户是否点赞
                hasLike = (holderUserId == null || userService.queryUserById(holderUserId) == null) ? false :
                        likeService.userHasLike(holderUserId, Constants.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("hasLike", hasLike);

                // 回复列表
                List<Comment> replyList = commentDao.queryCommentsByEntity(Constants.ENTITY_TYPE_COMMENT, comment.getId());
                // 回复的VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.querySimpleUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.querySimpleUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // 回复的赞
                        likeCount = likeService.findEntityLikeCount(Constants.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);

                        // 当前用户是否对回复点赞
                        hasLike = (holderUserId == null || userService.queryUserById(holderUserId) == null) ? false :
                                likeService.userHasLike(holderUserId, Constants.ENTITY_TYPE_COMMENT, reply.getId());
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

}
