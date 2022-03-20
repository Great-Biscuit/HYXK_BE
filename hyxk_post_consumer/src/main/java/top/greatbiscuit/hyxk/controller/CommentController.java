package top.greatbiscuit.hyxk.controller;

import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/20 16:05
 */
@RestController
@RequestMapping("/comment")
@ShenyuSpringMvcClient(path = "/comment/**")
public class CommentController {
}
