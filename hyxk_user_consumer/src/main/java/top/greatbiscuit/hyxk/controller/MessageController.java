package top.greatbiscuit.hyxk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.common.core.domain.R;
import top.greatbiscuit.hyxk.service.MessageService;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/29 21:01
 */
@RestController
@RequestMapping("/message")
@ShenyuSpringMvcClient(path = "/message/**")
public class MessageController {

    @DubboReference(version = "v1.0.0", timeout = 10000)
    private MessageService messageService;

    /**
     * 是否有未读消息
     *
     * @return
     */
    @RequestMapping("/hasUnread")
    public R hasUnread() {
        // 判断是否登录[不能进行登录拦截, 因为未登录用户主页也需要请求这一数据]
        if (!StpUtil.isLogin()) {
            return R.ok(false);
        }
        // 当前登录用户
        int holderUserId = StpUtil.getLoginIdAsInt();
        return R.ok(messageService.hasUnreadMessage(holderUserId));
    }

    /**
     * 返回消息首页需要的数据
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/home")
    public R getMessageHome() {
        int holderUserId = StpUtil.getLoginIdAsInt();
        Map<String, Object> homeData = new HashMap<>();
        // 各类通知的未读数
        homeData.put("noticeUnreadCount", messageService.getNoticeUnreadCount(holderUserId));
        // 会话列表
        homeData.put("conversationList", messageService.getLetterHome(holderUserId));
        return R.ok(homeData);
    }

    /**
     * 得到私信详情
     *
     * @param targetId 对方的Id
     * @return
     */
    @SaCheckLogin
    @PostMapping("/letter")
    public R letterDetail(int targetId) {
        int holderUserId = StpUtil.getLoginIdAsInt();
        // 组合出conversationId
        String conversationId = holderUserId < targetId ?
                holderUserId + "_" + targetId : targetId + "_" + holderUserId;
        // 把当前用户的Id放在msg里, 让前端分辨
        return R.ok(messageService.getLetterDetail(holderUserId, conversationId), holderUserId + "");
    }

    /**
     * 获取通知列表[点赞、收藏、评论]
     *
     * @param type
     * @return
     */
    @SaCheckLogin
    @PostMapping("/getNoticeList")
    public R getNoticeList(String type) {
        // 看topic是否合法
        if (!(type.equals(Constants.TOPIC_LIKE) ||
                type.equals(Constants.TOPIC_COLLECT) ||
                type.equals(Constants.TOPIC_COMMENT))) {
            return R.fail("通知类型不存在!");
        }
        return R.ok(messageService.getNoticeList(StpUtil.getLoginIdAsInt(), type));
    }

    /**
     * 获取关注通知列表
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("/getFollowNoticeList")
    public R getFollowNoticeList() {
        return R.ok(messageService.getFollowNoticeList(StpUtil.getLoginIdAsInt()));
    }

    /**
     * 发送私信
     *
     * @param toId
     * @param content
     * @return
     */
    @SaCheckLogin
    @PostMapping("/send")
    public R sendLetter(int toId, String content) {
        int holderUserId = StpUtil.getLoginIdAsInt();
        // html转义
        content = HtmlUtils.htmlEscape(content);
        String msg = messageService.sendLetter(holderUserId, toId, content);
        return msg == null ? R.ok() : R.fail(msg);
    }

}
