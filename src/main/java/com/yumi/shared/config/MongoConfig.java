package com.yumi.shared.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.yumi.shared.util.StringDecryptor;
import com.yumi.shared.util.StringEncryptor;

@Configuration
@EnableMongoRepositories(basePackages = "com.yumi.**.infrastructure.persistence")
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class MongoConfig {
  @Bean
  public MongoCustomConversions customConversions() {
    return new MongoCustomConversions(List.of(
        new StringEncryptor(),
        new StringDecryptor()));
  }
}