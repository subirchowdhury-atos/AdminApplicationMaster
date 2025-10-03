package com.adminapplicationmaster.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.SessionCookieConfig;

/**
 * Configures session cookie behavior
 */
@Configuration
public class SessionConfig {

    /**
     * Cookie configuration for sessions
     * Rails uses JSON serializer by default
     */
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            
            sessionCookieConfig.setName("JSESSIONID");
            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setSecure(false); // Set to true in production with HTTPS
            sessionCookieConfig.setPath("/");
            
            // Session timeout is configured in application.yml (3 minutes)
            servletContext.setSessionTimeout(3); // 3 minutes
        };
    }
}