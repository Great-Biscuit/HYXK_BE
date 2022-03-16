package top.greatbiscuit.hyxk;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户模块服务提供者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/10 15:42
 */
@EnableDubbo
@SpringBootApplication(scanBasePackages = {"top.greatbiscuit.hyxk", "top.greatbiscuit.common.redis"})
public class UserProvider {
    public static void main(String[] args) {
        SpringApplication.run(UserProvider.class, args);
    }
}
