package com.yumi.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.yumi.shared.security.JwtAuthFilter;
import com.yumi.shared.security.SessionContextFilter;
import com.yumi.shared.security.SessionRequiredFilter;
import java.util.List;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final ApplicationProps props;
  private final SessionRequiredFilter sessionRequiredFilter;
  private final SessionContextFilter  sessionContextFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            // --- públicas pero con sessionId obligatorio ---
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(POST, "/api/chat").permitAll()
            .requestMatchers("/api/cart/**").permitAll()
            .requestMatchers("/api/orders/anonymous/**").permitAll()

            // --- públicas ---
            .requestMatchers(OPTIONS, "/**").permitAll()
            .requestMatchers(GET, "/api/products/**", "/api/categories/**").permitAll()
            // .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

            // --- admin ---
            .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "INVENTORYMANAGER", "SHIPPINGMANAGER")

            // --- inventory ---
            .requestMatchers(POST, "/api/admin/products/**").hasAnyRole("ADMIN", "INVENTORYMANAGER")
            .requestMatchers(PUT, "/api/admin/products/**").hasAnyRole("ADMIN", "INVENTORYMANAGER")
            .requestMatchers(PATCH, "/api/admin/products/**").hasAnyRole("ADMIN", "INVENTORYMANAGER")
            .requestMatchers(DELETE, "/api/admin/products/**").hasAnyRole("ADMIN", "INVENTORYMANAGER")
            .requestMatchers("/api/admin/categories/**").hasAnyRole("ADMIN", "INVENTORYMANAGER")

            // --- shipping ---
            .requestMatchers(GET, "/api/admin/orders/**").hasAnyRole("ADMIN", "SHIPPINGMANAGER")
            .requestMatchers(PATCH, "/api/admin/orders/**").hasAnyRole("ADMIN", "SHIPPINGMANAGER")
            .requestMatchers(GET, "/api/admin/order-statuses").hasAnyRole("ADMIN", "SHIPPINGMANAGER")

            // --- autenticadas ---
            .requestMatchers("/api/orders/**", "/api/addresses/**").authenticated()

            .anyRequest().authenticated())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(sessionRequiredFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(sessionContextFilter, SessionRequiredFilter.class)
        .addFilterAfter(jwtAuthFilter, SessionContextFilter.class)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(props.getCors().getAllowedOrigins());
    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true);
    cfg.setExposedHeaders(List.of("Authorization"));
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}