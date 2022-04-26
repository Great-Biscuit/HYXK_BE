package top.greatbiscuit.hyxk;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后台管理服务生产者
 *
 * @Author: GreatBiscuit
 * @Date: 2022/4/26 18:30
 */
@EnableDubbo
@SpringBootApplication(scanBasePackages = {"top.greatbiscuit.hyxk", "top.greatbiscuit.common.redis"})
public class AdminProvider {
    public static void main(String[] args) {
        SpringApplication.run(AdminProvider.class, args);
    }
}
