package top.greatbiscuit.hyxk;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 帖子模块服务提供者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/9 17:28
 */
@EnableDubbo
@SpringBootApplication(scanBasePackages = {"top.greatbiscuit.hyxk", "top.greatbiscuit.common.redis"})
public class PostProvider {
    public static void main(String[] args) {
        SpringApplication.run(PostProvider.class, args);
    }
}
