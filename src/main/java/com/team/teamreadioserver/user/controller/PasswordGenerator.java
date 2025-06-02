package com.team.teamreadioserver.user.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 테스트
public class PasswordGenerator {
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String rawPassword = "4R29lk!!"; // 여기에 암호화할 비밀번호를 입력하세요 (예: "a123456789")
    String encodedPassword = encoder.encode(rawPassword);
    System.out.println("Encoded Password: " + encodedPassword);
  }

}
