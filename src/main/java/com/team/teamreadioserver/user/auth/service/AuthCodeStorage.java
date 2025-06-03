package com.team.teamreadioserver.user.auth.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthCodeStorage {

  private final Map<String, String> codeMap = new ConcurrentHashMap<>();

  public void store(String email, String code) {
    System.out.println("Store code for email: " + email + ", code: " + code);
    codeMap.put(email, code);
  }

  public boolean verify(String email, String code) {
    String storedCode = codeMap.get(email);
    System.out.println("Verify code for email: " + email + ", inputCode: " + code + ", storedCode: " + storedCode);
    return code.equals(codeMap.get(email));

  }
}
