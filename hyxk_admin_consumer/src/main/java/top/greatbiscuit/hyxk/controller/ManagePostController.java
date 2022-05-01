package top.greatbiscuit.hyxk.controller;

import org.apache.shenyu.client.springmvc.annotation.ShenyuSpringMvcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理帖子服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/5/1 20:35
 */
@RestController
@RequestMapping("/post")
@ShenyuSpringMvcClient(path = "/post/**")
public class ManagePostController {
}
