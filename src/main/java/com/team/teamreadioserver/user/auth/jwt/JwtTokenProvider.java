package com.team.teamreadioserver.user.auth.jwt;

import com.team.teamreadioserver.user.dto.UserInfoResponseDTO;
import com.team.teamreadioserver.user.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final long tokenValidity;
    private final UserMapper userMapper;  // 🔥 MyBatis Mapper 주입
    private Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration}") long tokenValidity,
                            UserMapper userMapper) {
        this.secretKey = secretKey;
        this.tokenValidity = tokenValidity;
        this.userMapper = userMapper;
    }

    // Key 객체 초기화
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 생성
    public String generateToken(String userId) {
        // MyBatis로 사용자 정보 조회
        UserInfoResponseDTO user = userMapper.selectUserById(userId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // JWT claims에 roles(권한) 포함 (예: "USER", "ADMIN")
        String role = user.getUserRole(); // DTO에 roles 필드가 있어야 함

        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT에서 사용자 ID 추출
    public String getUserIdFromJWT(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    // JWT에서 claims 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Key 객체 사용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getRoleFromJWT(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String authToken) {
        try {
            getClaimsFromToken(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

