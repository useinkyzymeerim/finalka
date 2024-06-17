package com.finalka.config;

import com.finalka.filter.UrlDecodeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<UrlDecodeFilter> loggingFilter(){
        FilterRegistrationBean<UrlDecodeFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new UrlDecodeFilter());
        registrationBean.addUrlPatterns("/api/comminis/*");

        return registrationBean;
    }
}