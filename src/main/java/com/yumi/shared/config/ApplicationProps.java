package com.yumi.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app")
public class ApplicationProps {

  private final Jwt jwt = new Jwt();
  private final Cors cors = new Cors();
  private final Cloudinary cloudinary = new Cloudinary();

  @Data
  public static class Jwt {
    private String secret;
    private long expiration; // en segundos
    private List<String> roles;
  }

  @Data
  public static class Cors {
    private List<String> allowedOrigins;
  }

  @Data
  public static class Cloudinary {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
  }
}