package top.greatbiscuit.hyxk.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import top.greatbiscuit.hyxk.dao.PostDao;
import top.greatbiscuit.hyxk.entity.Post;
import top.greatbiscuit.hyxk.service.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务生产者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/26 18:57
 */
@DubboService(version = "v1.0.0")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    /**
     * 保存帖子
     *
     * @param postId
     */
    @Override
    public void savePost(int postId) {
        elasticsearchRestTemplate.save(postDao.queryById(postId));
    }

    /**
     * 删除帖子
     *
     * @param postId
     */
    @Override
    public void deletePost(int postId) {
        elasticsearchRestTemplate.delete(String.valueOf(postId), Post.class);
    }

    /**
     * 查询
     *
     * @param text
     * @param current
     * @param limit
     * @param type    帖子类型[为空时则查询所有帖子]
     * @return
     */
    @Override
    public List<Post> searchPostList(String text, int current, int limit, Integer type) {
        NativeSearchQuery searchQuery;

        // 没有指定类型就全部查询, 指定了就查询固定类型
        if (type == null) {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.multiMatchQuery(text, "title", "htmlContent"))
                    .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                    .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                    .withPageable(PageRequest.of(current, limit))
                    .withHighlightFields(
                            new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                            new HighlightBuilder.Field("htmlContent").preTags("<em>").postTags("</em>")
                    )
                    .build();
        } else {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.multiMatchQuery(text, "title", "htmlContent"))
                            .must(QueryBuilders.matchQuery("type", type)))
                    .withSorts(SortBuilders.fieldSort("top").order(SortOrder.DESC))
                    .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                    .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                    .withPageable(PageRequest.of(current, limit))
                    .withHighlightFields(
                            new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>")
                    )
                    .build();
        }

        List<SearchHit<Post>> searchHits = elasticsearchRestTemplate.search(searchQuery, Post.class).getSearchHits();

        // 处理查询结果
        if (searchHits.isEmpty()) {
            return null;
        }
        List<Post> postList = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Post post = (Post) hit.getContent();
            Map<String, Object> highlightFields = hit.getHighlightFields();
            // 替换为高亮内容
            if (highlightFields.containsKey("title")) {
                post.setTitle((String) hit.getHighlightField("title").get(0));
            }
            // 由于内容自带样式, 所以页面不能再进行内容展示
            post.setHtmlContent(null);
            post.setMarkdownContent(null);
            postList.add(post);
        }

        return postList;
    }
}
