package com.atguigu.gmall.configuration;

import com.atguigu.gmall.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WebMvcConfiguration
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-18
 * @Description:
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
