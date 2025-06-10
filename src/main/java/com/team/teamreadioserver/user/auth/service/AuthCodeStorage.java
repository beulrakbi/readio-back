package com.team.teamreadioserver.user.auth.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 비밀번호 찾기 시 이메일 인증할때 그 인증번호 저장
@Component
public class AuthCodeStorage {

  private final Map<String, String> codeMap = new ConcurrentHashMap<>();

  public void store(String email, String code) {
    email = email.toLowerCase();  // 일관 처리
    System.out.println("Store code for email: " + email + ", code: " + code);
    codeMap.put(email, code);
  }

  public boolean verify(String email, String code) {
    email = email.toLowerCase();  // 일관 처리
    String storedCode = codeMap.get(email);
    System.out.println("Verify code for email: " + email + ", inputCode: " + code + ", storedCode: " + storedCode);
    return code.equals(codeMap.get(email));

  }
}
