package top.greatbiscuit.hyxk;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后台管理服务消费者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/26 18:26
 */
@EnableDubbo
@SpringBootApplication
public class AdminConsumer {
    public static void main(String[] args) {
        SpringApplication.run(AdminConsumer.class, args);
    }
}
