package com.team.teamreadioserver.user.email.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;
  private static final String senderEmail = "qhrud647461@gmail.com";

  // 랜덤으로 숫자 생성
  public String createNumber() {
    Random random = new Random();
    StringBuilder key = new StringBuilder();

    for (int i = 0; i < 8; i++) { // 인증 코드 8자리
      int index = random.nextInt(3); // 0~2까지 랜덤, 랜덤값으로 switch문 실행

      switch (index) {
        case 0 -> key.append((char) (random.nextInt(26) + 97)); // 소문자
        case 1 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
        case 2 -> key.append(random.nextInt(10)); // 숫자
      }
    }
    return key.toString();
  }

  // 이메일 발송 레이아웃(?)
  public MimeMessage createMail(String mail, String number) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();

    message.setFrom(senderEmail);
    message.setRecipients(MimeMessage.RecipientType.TO, mail);
    message.setSubject("READIO 인증번호 안내");

    String body =
        "<!DOCTYPE html>" +
            "<html lang='ko'>" +
            "<head><meta charset='UTF-8'><title>READIO 인증번호 안내</title></head>" +
            "<body style=\"font-family: 'Pretendard Variable', sans-serif; background-color: #f2f2f2; padding: 30px;\">" +
            "<div style='max-width:600px; margin:auto; background-color:#fff; padding:30px; border-radius:10px; box-shadow:0 2px 8px rgba(0,0,0,0.1);'>" +
            "<div style='background-color:#808467; text-align:center; padding:15px;'>" +
            "<a style='font-size:40px; color:#F6EEB6; font-weight:bolder;'>READIO</a>" +
            "</div><br/>" +
            "<p style='font-size:16px; color:#444;'><h3>'비밀번호 재설정'을 위한 인증번호입니다.</h3>" +
            "<strong>READIO</strong>에서 요청하신 인증번호를 안내드립니다.<br/>" +
            "비밀번호 재설정 페이지에서 아래 인증번호를 입력하여 비밀번호 재설정이 가능합니다.</p>" +
            "<div style='margin:30px 0; padding:20px; border:2px dashed #808467; text-align:center;'>" +
            "<p style='margin:0; font-size:18px; color:#333;'>인증번호</p>" +
            "<h1 style='margin:10px 0; font-size:40px; color:#131313;'>" + number + "</h1>" +
            "</div>" +
            "<p style='font-size:16px; color:#555;'>※ 인증번호는 30분간 유효합니다.</p><br/><br/>" +
            "<hr/><p style='font-size:13px; color:#333;'>본 메일은 발신전용입니다.<br/>Copyright © READIO Corp.All rights reserved.</p>" +
            "</div></body></html>";

    message.setContent(body, "text/html; charset=UTF-8");
    return message;
  }

  // 메일 발송
  public String sendSimpleMessage(String sendEmail) throws MessagingException {
    String number = createNumber(); // 랜덤 인증번호 생성
    System.out.println("메일 발송: email = " + sendEmail + ", code = " + number); // 로그 출력

    MimeMessage message = createMail(sendEmail, number); // 메일 생성
    try {
      javaMailSender.send(message); // 메일 발송
    } catch (MailException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
    }

    return number; // 생성된 인증번호 반환
  }


}
