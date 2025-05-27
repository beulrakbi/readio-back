package com.team.teamreadioserver.profile.repository;

import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.entity.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {

    // 프로필에 연결된 이미지 삭제용
    void deleteByProfile(Profile profile);
    Optional<ProfileImg> findByProfile(Profile profile);


}