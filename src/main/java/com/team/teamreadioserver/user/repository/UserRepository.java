package com.team.teamreadioserver.user.repository;

import com.team.teamreadioserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUserEmail(String userEmail);

}
