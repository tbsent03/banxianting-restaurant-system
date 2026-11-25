package com.project.platform.config;

import com.project.platform.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                //允许直接访问的接口
                .excludePathPatterns(
                        "/common/login",
                        "/common/register",
                        "/common/retrievePassword",
                        "/file/**",
                       // 顾客端先浏览、后登录需要的读接口
                        "/category/list",
                        "/dish/page",
                        "/dish/selectById/**",
                        "/table/token/**",
                        "/review/page"
                );
    }
}
