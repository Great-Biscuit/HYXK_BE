package top.greatbiscuit.hyxk;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: GreatBiscuit
 * @Date: 2022/1/10 17:36
 */
@EnableDubbo
@SpringBootApplication
public class UserConsumer {
    public static void main(String[] args) {
        SpringApplication.run(UserConsumer.class, args);
    }
}
