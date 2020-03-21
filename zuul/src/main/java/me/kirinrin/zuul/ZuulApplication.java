package me.kirinrin.zuul;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Kirinrin
 */
@EnableDiscoveryClient
@EnableZuulProxy
@EnableCaching
@SpringBootApplication
@Slf4j
public class ZuulApplication {

    public static void main(String[] args) {
        log.info("设置系统时区UTC+8 {}", new Date());
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(ZuulApplication.class, args);
    }

    @PostConstruct
    void setDefaultTimezone() {
        log.info("设置系统时区UTC+8 {}", new Date());
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
