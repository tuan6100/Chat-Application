package com.chat.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.chat.app.repository.jpa", "com.chat.app.security"})
public class JpaConfig {
}