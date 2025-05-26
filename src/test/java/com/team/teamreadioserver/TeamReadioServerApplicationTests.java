package com.team.teamreadioserver;

import com.team.teamreadioserver.config.PasswordConfig;
import com.team.teamreadioserver.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class TeamReadioServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testPasswordEncoder() {
        String raw = "userInputPassword";
        String encoded = userMapper.findByUserId("testUser").getUserPwd();
        System.out.println("비밀번호 일치 여부:" + passwordEncoder.matches(raw, encoded));
    }

}
