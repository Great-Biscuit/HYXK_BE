package top.greatbiscuit.hyxk.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子表(Post)实体类
 *
 * @author makejava
 * @since 2022-03-19 17:56:35
 */
@Document(indexName = "post")
public class Post implements Serializable {
    private static final long serialVersionUID = 117918344050047135L;
    /**
     * 主键
     */
    @Id
    private Integer id;
    /**
     * 作者
     */
    @Field(type = FieldType.Integer)
    private Integer userId;
    /**
     * 标题
     */
    // 前面存储器是存储时（存储更多分词）  后面那个是搜索时(智能分词，防止过多无用词)
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    /**
     * 类型[0-文章 1-问答]
     */
    @Field(type = FieldType.Integer)
    private Integer type;
    /**
     * Markdown内容
     */
    @Field(type = FieldType.Auto)
    private String markdownContent;
    /**
     * HTML内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String htmlContent;
    /**
     * 状态[0-正常 1-加精 2-删除]
     */
    @Field(type = FieldType.Integer)
    private Integer state;
    /**
     * 置顶[0-不置顶 1-置顶]
     */
    @Field(type = FieldType.Integer)
    private Integer top;
    /**
     * 官方[0-非官方 1-官方]
     */
    @Field(type = FieldType.Integer)
    private Integer official;
    /**
     * 最佳回复
     */
    @Field(type = FieldType.Integer)
    private Integer bestCommentId;
    /**
     * 评论数
     */
    @Field(type = FieldType.Integer)
    private Integer comments;
    /**
     * 文章头图
     */
    @Field(type = FieldType.Auto)
    private String headImg;
    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;
    /**
     * 分数
     */
    @Field(type = FieldType.Double)
    private Double score;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getOfficial() {
        return official;
    }

    public void setOfficial(Integer official) {
        this.official = official;
    }

    public Integer getBestCommentId() {
        return bestCommentId;
    }

    public void setBestCommentId(Integer bestCommentId) {
        this.bestCommentId = bestCommentId;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", markdownContent='" + markdownContent + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", state=" + state +
                ", top=" + top +
                ", official=" + official +
                ", bestCommentId=" + bestCommentId +
                ", comments=" + comments +
                ", headImg='" + headImg + '\'' +
                ", createTime=" + createTime +
                ", score=" + score +
                '}';
    }
}

