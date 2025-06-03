package com.team.teamreadioserver.user.email.controller;

import com.team.teamreadioserver.user.auth.service.AuthCodeStorage;
import com.team.teamreadioserver.user.email.dto.MailDTO;
import com.team.teamreadioserver.user.email.dto.PasswordResetDTO;
import com.team.teamreadioserver.user.email.dto.VerifyCodeDTO;
import com.team.teamreadioserver.user.email.service.MailService;
import com.team.teamreadioserver.user.mapper.UserMapper;
import com.team.teamreadioserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "이메일 인증 API", description = "이메일로 인증번호를 전송합니다.") // Swagger 그룹 태그
@RequestMapping("/api/email")
public class MailController {

  private final MailService mailService;
  private final AuthCodeStorage authCodeStorage;
  private final UserService userService;
  private final UserMapper userMapper;


//  @ResponseBody
//  @PostMapping("/sendCode") // 프론트 매핑 완료
//  @Operation(summary = "이메일 인증 요청", description = "이메일 주소로 인증 번호를 보냅니다.")
//  public String emailCheck(@RequestBody MailDTO mailDTO) throws MessagingException, UnsupportedEncodingException {
//    String authCode = mailService.sendSimpleMessage(mailDTO.getEmail());
//    authCodeStorage.store(mailDTO.getEmail(), authCode);  // 여기서 저장!
//    return authCode; // Response body에 값을 반환
//  }

  @PostMapping("/sendCode")
  @Operation(summary = "이메일 인증 요청", description = "아이디와 이메일이 일치하면 이메일 주소로 인증 번호를 보냅니다.")
  public ResponseEntity<?> emailCheck(@RequestBody MailDTO mailDTO) throws MessagingException {
    int match = userMapper.existsByUserIdAndEmail(mailDTO.getUserId(), mailDTO.getEmail());

    if (match != 1) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("message", "입력한 아이디와 이메일이 일치하지 않습니다."));
    }

    String authCode = mailService.sendSimpleMessage(mailDTO.getEmail());
    authCodeStorage.store(mailDTO.getEmail(), authCode);
    return ResponseEntity.ok(authCode);
  }


  @Operation(summary = "이메일 인증 확인", description = "이메일 인증번호가 일치한지 확인합니다.")
  @PostMapping("/verifyCode")
  public ResponseEntity<String> verifyCode(@RequestBody VerifyCodeDTO dto) {
    boolean result = authCodeStorage.verify(dto.getEmail(), dto.getCode());
    if (!result) return ResponseEntity.status(400).body("인증번호가 올바르지 않습니다.");
    return ResponseEntity.ok("인증 성공");
  }

  @Operation(summary = "이메일 인증 후 비밀번호 재설정", description = "이메일 인증번호가 일치한지 확인합니다.")
  @PostMapping("/resetPassword")
  public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO dto) {
    boolean success = userService.resetPassword(dto.getUserId(), dto.getNewPassword());
    if (!success) return ResponseEntity.status(400).body("비밀번호 재설정 실패");
    return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
  }


}
