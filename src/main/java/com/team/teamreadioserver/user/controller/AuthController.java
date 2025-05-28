package com.team.teamreadioserver.user.controller;//package com.team.teamreadioserver.user.controller;

import com.team.teamreadioserver.user.auth.jwt.JwtTokenProvider;
import com.team.teamreadioserver.user.dto.JwtResponseDTO;
import com.team.teamreadioserver.user.dto.LoginRequestDTO;
import com.team.teamreadioserver.user.dto.UserInfoResponseDTO;
import com.team.teamreadioserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

//로그인 엔드포인트
@RestController
@RequestMapping("/users")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider tokenProvider;
  @Autowired
  private UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserInfoResponseDTO> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // userDetails.getUsername() = 로그인한 사용자 ID
    String userId = userDetails.getUsername();

    // 예시: 서비스에서 사용자 정보 조회
    UserInfoResponseDTO userInfo = userService.getUserInfo(userId);
    if (userInfo == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(userInfo);
  }


  @Operation(summary = "로그인", description = "회원이 로그인을 합니다.")
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
    System.out.println("로그인 요청 username: " + loginRequestDTO.getUsername());

    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
      );
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = tokenProvider.generateToken(authentication.getName());

      // 로그인한 계정 권한 로그 찍기
      Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
      System.out.println("로그인한 계정 권한: " + authorities);

      // JwtResponseDTO를 사용해서 응답
      JwtResponseDTO jwtResponse = new JwtResponseDTO(token);
      jwtResponse.setUserId(userDetails.getUsername()); // userId
      jwtResponse.setUserName(userDetails.getUsername());

      System.out.println("로그인 성공: " + userDetails.getUsername() + ", 생성된 토큰: " + token); // 토큰 값 확인하기

      return ResponseEntity.ok(jwtResponse); // JwtResponseDTO 객체 반환

    } catch (UsernameNotFoundException e) { // <--- 예외를 구체적으로 처리
      System.err.println("로그인 실패 (사용자 없음): " + e.getMessage()); // System.err로 변경
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 아이디를 찾을 수 없습니다.");

    } catch (BadCredentialsException e) { // <--- 예외를 구체적으로 처리
      System.err.println("로그인 실패 (비밀번호 불일치): " + e.getMessage()); // System.err로 변경
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 비밀번호가 올바르지 않습니다.");

    } catch (Exception e) { // <--- 그 외 예상치 못한 예외
      e.printStackTrace(); // 스택 트레이스 출력 (가장 중요)
      System.err.println("로그인 실패 (예상치 못한 오류): " + e.getMessage()); // System.err로 변경
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 실패: 서버 오류가 발생했습니다."); // 500으로 변경
    }
  }

}
