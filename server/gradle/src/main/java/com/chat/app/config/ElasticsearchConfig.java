package com.chat.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.chat.app.repository.elasticsearch")
public class ElasticsearchConfig {
}