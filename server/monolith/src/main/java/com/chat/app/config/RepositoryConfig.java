package com.chat.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.chat.app.repository.jpa")
@EnableRedisRepositories(basePackages = "com.chat.app.repository.redis")
@EnableElasticsearchRepositories(basePackages = "com.chat.app.repository.elasticsearch")
public class RepositoryConfig {
}
