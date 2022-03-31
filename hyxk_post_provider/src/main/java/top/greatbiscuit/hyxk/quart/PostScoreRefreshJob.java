package top.greatbiscuit.hyxk.quart;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.BoundSetOperations;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.redis.service.RedisService;
import top.greatbiscuit.common.redis.utils.RedisKeyUtil;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 帖子分数更新任务
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/31 20:32
 */
public class PostScoreRefreshJob implements Job {

    @Autowired
    private RedisService redisService;

    @Autowired
    private PostDao postDao;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    // 系统起始时间
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化系统起始时间失败!", e);
        }
    }

    /**
     * 执行任务 刷新帖子分数[分数以Set存在Redis里]
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations boundSetOperations = redisService.getBoundSetOperations(redisKey);

        // 若其中为空则不进行刷新, 任务到此结束
        if (boundSetOperations.size() == 0) {
            return;
        }

        // 如果不为空, 则开始刷新帖子分数任务
        while (boundSetOperations.size() > 0) {
            this.refresh((Integer) boundSetOperations.pop());
        }
    }

    /**
     * 刷新帖子分数
     *
     * @param postId
     */
    private void refresh(int postId) {
        Post post = postDao.queryById(postId);
        //防止 帖子被删
        if (post == null) {
            return;
        }
        // 是否加精
        boolean wonderful = post.getState() == 1;
        // 评论数量
        int commentCount = post.getComments();
        // 点赞数量
        String likeKey = RedisKeyUtil.getEntityLikeKey(Constants.ENTITY_TYPE_POST, postId);
        long likeCount = redisService.getSetSize(likeKey);
        //TODO: 收藏加分

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 权重 + 天数
        double score = Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        // 更新帖子分数
        postDao.updateScore(postId, score);
        post.setScore(score);
        // 同步到ES
        elasticsearchRestTemplate.save(post);
    }
}
