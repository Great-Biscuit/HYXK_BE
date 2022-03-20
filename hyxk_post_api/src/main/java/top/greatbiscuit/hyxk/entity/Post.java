package top.greatbiscuit.hyxk.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子表(Post)实体类
 *
 * @author makejava
 * @since 2022-03-19 17:56:35
 */
public class Post implements Serializable {
    private static final long serialVersionUID = 117918344050047135L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 作者
     */
    private Integer userId;
    /**
     * 标题
     */
    private String title;
    /**
     * 类型[0-文章 1-问答
     */
    private Integer type;
    /**
     * Markdown内容
     */
    private String markdownContent;
    /**
     * HTML内容
     */
    private String htmlContent;
    /**
     * 状态[0-正常 1-加精 2-删除]
     */
    private Integer state;
    /**
     * 置顶[0-不置顶 1-置顶]
     */
    private Integer top;
    /**
     * 官方[0-非官方 1-官方]
     */
    private Integer official;
    /**
     * 最佳回复
     */
    private Integer bestCommentId;
    /**
     * 评论数
     */
    private Integer comments;
    /**
     * 文章头图
     */
    private String headImg;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 分数
     */
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

}

