package com.example.souvenirstore.interceptor;

import com.example.souvenirstore.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private UserInterceptor userInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/product/**")
                .addPathPatterns("/order/**")
                .addPathPatterns("/cart/**")
        ;

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
        ;
    }
}
