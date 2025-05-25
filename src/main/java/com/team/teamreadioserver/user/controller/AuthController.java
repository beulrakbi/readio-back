package com.team.teamreadioserver.user.controller;//package com.team.teamreadioserver.user.controller;

import com.team.teamreadioserver.user.auth.jwt.JwtTokenProvider;
import com.team.teamreadioserver.user.dto.JwtResponseDTO;
import com.team.teamreadioserver.user.dto.LoginRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

//로그인 엔드포인트
@RestController
@RequestMapping("/users")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
    System.out.println("로그인 요청 username: " + loginRequestDTO.getUsername());
    try {

      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
      );
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = tokenProvider.generateToken(authentication.getName());
      System.out.println("로그인 성공: " + userDetails.getUsername());

      return ResponseEntity.ok(new JwtResponseDTO(token));
    } catch (Exception e) {
      System.out.println("로그인 실패: " + e.getMessage());
      return ResponseEntity.status(401).body("로그인 실패: " + e.getMessage());
    }
  }

}
