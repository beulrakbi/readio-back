package com.team.teamreadioserver.user.controller;//package com.team.teamreadioserver.user.controller;
//
//import com.team.teamreadioserver.user.auth.jwt.JwtTokenProvider;
//import com.team.teamreadioserver.user.dto.LoginRequestDTO;
//import com.team.teamreadioserver.user.entity.User;
//import com.team.teamreadioserver.user.service.UserService;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/users")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final UserService  userService;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Operation(summary = "로그인 요청", description = "사용자가 로그인한다.")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
//        User user = userService.login(loginRequestDTO);
//        if(user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserRole().name());
//        return ResponseEntity.ok(Map.of("accessToken", token));
//    }
//}
