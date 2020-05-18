package com.atguigu.gmall;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.atguigu.gmall.manage.mapper")
public class GmallManageServiceApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(GmallManageServiceApplication.class, args);
        ConfigurableListableBeanFactory beanFactory = run.getBeanFactory();
        Object dataSource = beanFactory.getBean("dataSource");
        String name = dataSource.getClass().getName();
        System.out.println(name);
    }

}
