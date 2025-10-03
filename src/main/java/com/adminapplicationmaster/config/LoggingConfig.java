package com.adminapplicationmaster.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filters sensitive parameters from logs
 */
@Configuration
public class LoggingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
    }

    @Slf4j
    static class LoggingInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, 
                                HttpServletResponse response, 
                                Object handler) {
            // Log request (sensitive parameters will be filtered in logback.xml)
            log.debug("Request: {} {}", request.getMethod(), request.getRequestURI());
            return true;
        }
    }
}