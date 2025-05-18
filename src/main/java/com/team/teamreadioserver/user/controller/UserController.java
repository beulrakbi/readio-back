package com.team.teamreadioserver.user.controller;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "회원 API", description = "회원가입 관련 API")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "회원가입", description = "신규 사용자를 등록한다.")
  @PostMapping("join")
  public ResponseEntity<String> join(@RequestBody JoinRequestDTO joinRequestDTO) {
    if (!userService.isUserIdAvailable(joinRequestDTO.getUserId())){
      return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
    }
    if(!userService.isEmailAvailable(joinRequestDTO.getUserEmail())){
      return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
    }

    userService.joinUser(joinRequestDTO);
    return ResponseEntity.ok("회원가입 성공");
  }

  @GetMapping("/test")
  public String test() {
    return "test";
  }


}
