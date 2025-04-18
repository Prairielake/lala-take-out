package com.lala;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@MapperScan("com.lala.mapper")
public class LalaApplication {
    public static void main(String[] args) {
        SpringApplication.run(LalaApplication.class, args);
        log.info("server started");
    }
}
