package top.greatbiscuit.hyxk;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/21 16:25
 */
@EnableDubbo
@SpringBootApplication
public class UserConsumer {
    public static void main(String[] args) {
        SpringApplication.run(UserConsumer.class, args);
    }
}
