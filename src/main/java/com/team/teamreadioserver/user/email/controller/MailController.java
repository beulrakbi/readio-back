package com.team.teamreadioserver.user.email.controller;

import com.team.teamreadioserver.user.email.dto.MailDTO;
import com.team.teamreadioserver.user.email.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@Tag(name = "이메일 인증 API", description = "이메일로 인증번호를 전송합니다.") // Swagger 그룹 태그
@RequestMapping("/api/email")
public class MailController {

  private final MailService mailService;


  @ResponseBody
  @PostMapping("/sendCode") // 프론트 매핑 완료
  @Operation(summary = "이메일 인증 요청", description = "이메일 주소로 인증 번호를 보냅니다.")
  public String emailCheck(@RequestBody MailDTO mailDTO) throws MessagingException, UnsupportedEncodingException {
    String authCode = mailService.sendSimpleMessage(mailDTO.getEmail());
    return authCode; // Response body에 값을 반환
  }




}
