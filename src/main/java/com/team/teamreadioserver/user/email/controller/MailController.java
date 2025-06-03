package com.team.teamreadioserver.user.email.controller;

import com.team.teamreadioserver.user.email.dto.MailDTO;
import com.team.teamreadioserver.user.email.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@Tag(name = "이메일 인증 API", description = "이메일로 인증번호를 전송합니다.") // Swagger 그룹 태그
public class MailController {

  private final MailService mailService;

  @ResponseBody
  @PostMapping("/api/email/sendCode") // 이 부분은 각자 바꿔주시면 됩니다.
  @Operation(summary = "이메일 인증 요청", description = "이메일 주소로 인증 번호를 보냅니다.")
  public String emailCheck(@RequestBody MailDTO mailDTO) throws MessagingException, UnsupportedEncodingException {
    String authCode = mailService.sendSimpleMessage(mailDTO.getEmail());
    return authCode; // Response body에 값을 반환
  }


}
