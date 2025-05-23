//package com.team.teamreadioserver.user.auth.jwt;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtTokenProvider {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    private final long expirationTime = 1000 * 60 * 60; // 1시간
//
//    public String generateToken(String userId, String role) {
//        return Jwts.builder()
//                .setSubject(userId)
//                .claim("role", role)
//                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }
//
//    public String getUserIdFromToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            // Spring Boot 3 이상에서는 아래처럼 ParserBuilder 사용 권장함...
//            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
//
