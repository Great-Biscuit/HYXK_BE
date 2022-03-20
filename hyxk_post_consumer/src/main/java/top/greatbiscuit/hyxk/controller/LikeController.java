package top.greatbiscuit.hyxk.controller;

import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/20 16:03
 */
@RestController
@RequestMapping("/like")
@ShenyuSpringMvcClient(path = "/like/**")
public class LikeController {
}
