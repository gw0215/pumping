package com.pumping.global.config;

import com.pumping.global.common.reslover.AuthMemberArgumentResolver;
import com.pumping.global.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthMemberArgumentResolver authMemberArgumentResolver;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public WebConfig(AuthMemberArgumentResolver authMemberArgumentResolver) {
        this.authMemberArgumentResolver = authMemberArgumentResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile-image/**")
                .addResourceLocations("file:" + uploadDir);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/boards/*");
        registration.addUrlPatterns("/routines/*");
        registration.addUrlPatterns("/routines/*");
        registration.addUrlPatterns("/members/*");
        registration.addUrlPatterns("/inbody/*");
        registration.addUrlPatterns("/exercise-history/*");
        registration.addUrlPatterns("/verify-password/*");
        return registration;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authMemberArgumentResolver);
    }

}
