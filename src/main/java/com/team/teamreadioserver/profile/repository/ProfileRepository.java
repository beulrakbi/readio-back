package com.team.teamreadioserver.profile.repository;

import com.team.teamreadioserver.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser_UserId(String userId);
    // 필명 중복 확인용 (기본 필명 생성시 필요)
    boolean existsByPenName(String penName);

    Profile findByProfileId(Long profileId);
    @Query("SELECT p.profileId FROM Profile p WHERE p.user.userId = :userId")
    Optional<Long> findProfileIdByUserId(@Param("userId") String userId);
}
