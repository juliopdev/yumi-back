package com.yumi.shared.config;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

  private final ApplicationProps props;

  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(Map.of(
        "cloud_name", props.getCloudinary().getCloudName(),
        "api_key", props.getCloudinary().getApiKey(),
        "api_secret", props.getCloudinary().getApiSecret()));
  }
}