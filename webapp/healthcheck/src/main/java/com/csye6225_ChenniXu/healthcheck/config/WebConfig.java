package com.csye6225_ChenniXu.healthcheck.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<NoPayloadFilter> noPayloadFilter() {
        FilterRegistrationBean<NoPayloadFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new NoPayloadFilter());
        registrationBean.addUrlPatterns("/healthz/*");
        return registrationBean;
    }
}