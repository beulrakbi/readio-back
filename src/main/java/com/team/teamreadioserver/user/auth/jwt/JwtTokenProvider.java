package com.team.teamreadioserver.user.auth.jwt;//package com.team.teamreadioserver.user.auth.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expirationMs;

    private Key getSigningKey() {
      return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

//    private final long expirationTime = 1000 * 60 * 60; // 1시간

   // 토큰 생성
  public String generateToken(String username) {
      Date now = new Date();
      Date expiryDate = new Date(now.getTime() + expirationMs);
      return Jwts.builder()
          .setSubject(username)
          .setIssuedAt(now)
          .setExpiration(expiryDate)
          .signWith(getSigningKey(), SignatureAlgorithm.HS512)
          .compact();
  }

  @PostConstruct
  public void init() {
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      if(keyBytes.length < 32) {
        throw new IllegalArgumentException("Secret key must be at least 32 bytes");
      }
  }

  public String getUsernameFromToken(String token) {
      return Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
  }

  public boolean validateToken(String token) {
      try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
        return true;
      } catch (JwtException ex) {
        return false;
      }
  }


}

