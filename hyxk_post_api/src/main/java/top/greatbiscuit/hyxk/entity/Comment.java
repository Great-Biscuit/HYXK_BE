package top.greatbiscuit.hyxk.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论表(Comment)实体类
 *
 * @author makejava
 * @since 2022-03-21 18:45:29
 */
public class Comment implements Serializable {
    private static final long serialVersionUID = 250021507350455527L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 进行评论的用户ID
     */
    private Integer userId;
    /**
     * 被评论的实体类型
     */
    private Integer entityType;
    /**
     * 被评论的实体ID
     */
    private Integer entityId;
    /**
     * 被评论的目标ID[在对评论进行评论时应用]
     */
    private Integer targetId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论状态[0-正常 1-删除]
     */
    private Integer state;
    /**
     * 评论时间
     */
    private Date createTime;


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

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}

