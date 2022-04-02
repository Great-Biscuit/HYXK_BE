package top.greatbiscuit.hyxk.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表(User)实体类
 *
 * @Author GreatBiscuit
 * @Date 2022-01-10 15:30:25
 */
public class User implements Serializable {
    private static final long serialVersionUID = -44286461385259871L;
    /**
     * 用户编号
     */
    private Integer id;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 加密盐值
     */
    private String salt;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户头像地址
     */
    private String headerUrl;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别[0-未知 1-男 2-女]
     */
    private Integer gender;
    /**
     * 个性签名
     */
    private String signature;
    /**
     * 用户类型[0-普通 1...9-对应各版主 999-超级管理员 886-注销]
     */
    private Integer type;
    /**
     * 创建时间
     */
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", headerUrl='" + headerUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", signature='" + signature + '\'' +
                ", type=" + type +
                ", createTime=" + createTime +
                '}';
    }
}

