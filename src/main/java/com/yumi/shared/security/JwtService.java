package com.yumi.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.yumi.shared.config.ApplicationProps;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final ApplicationProps props;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(props.getJwt().getSecret().getBytes());
  }

  public String generateToken(String email, String sessionId, String roles) {
    HashMap<String, Object> claims = new HashMap<>();
    claims.put("sessionId", sessionId);
    claims.put("roles", roles);
    return Jwts.builder()
        .claims(claims)
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + props.getJwt().getExpiration() * 1000))
        .signWith(getSigningKey())
        .compact();
  }

  public String extractUsername(String token) {
    return getClaims(token).getSubject();
  }

  public boolean isTokenValid(String token) {
    if (token.isBlank() || !token.contains(".") || token.split("\\.").length != 3) {
      return false;
    }
    return getClaims(token).getExpiration().after(new Date());
  }

  protected Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}