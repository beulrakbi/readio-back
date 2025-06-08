package com.team.teamreadioserver.user.repository;

import com.team.teamreadioserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUserEmail(String userEmail);

    Optional<User> findByUserId(String userId);





}
