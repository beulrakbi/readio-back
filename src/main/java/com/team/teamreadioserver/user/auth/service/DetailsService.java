package com.team.teamreadioserver.user.auth.service;

import com.team.teamreadioserver.user.auth.model.DetailsUser;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUserId(username);
        if(user == null) throw new UsernameNotFoundException("User Not Found!");
        return new DetailsUser(user);
        }
    }

