package top.greatbiscuit.hyxk;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 帖子服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/20 13:31
 */
@EnableDubbo
@SpringBootApplication
public class PostConsumer {
    public static void main(String[] args) {
        SpringApplication.run(PostConsumer.class, args);
    }
}
