package com.atguigu.gmall;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.atguigu.gmall.pay.mapper")
public class GmallPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPayApplication.class, args);
    }

}
