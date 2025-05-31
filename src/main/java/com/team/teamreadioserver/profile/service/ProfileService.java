package com.team.teamreadioserver.profile.service;

import com.team.teamreadioserver.profile.dto.ProfileRequestDTO;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.entity.ProfileImg;
import com.team.teamreadioserver.profile.enums.PrivateStatus;
import com.team.teamreadioserver.profile.repository.ProfileImgRepository;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileImgRepository profileImgRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    // 등록 + 수정
    @Transactional
    public void upsertProfile(ProfileRequestDTO profileRequestDTO) {
        User user = userRepository.findById(profileRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUser_UserId(user.getUserId()).orElse(null);

        String penName = (profileRequestDTO.getPenName() == null || profileRequestDTO.getPenName().isEmpty())
                ? generateDefaultPenName()
                : profileRequestDTO.getPenName();

        if (profile == null) {
            profile = Profile.builder()
                    .user(user)
                    .penName(penName)
                    .biography(profileRequestDTO.getBiography())
                    .isPrivate("PRIVATE".equalsIgnoreCase(profileRequestDTO.getIsPrivate())
                            ? PrivateStatus.PRIVATE : PrivateStatus.PUBLIC)
                    .createdAt(LocalDateTime.now())
                    .build();
        } else {
            profile.setPenName(penName);
            profile.setBiography(profileRequestDTO.getBiography());
            profile.setIsPrivate("PRIVATE".equalsIgnoreCase(profileRequestDTO.getIsPrivate())
                    ? PrivateStatus.PRIVATE : PrivateStatus.PUBLIC);
        }

        profileRepository.save(profile);

        MultipartFile image = profileRequestDTO.getImage();
        if (image != null && !image.isEmpty()) {
            String originalName = image.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String saveName = user.getUserId() + "_IMG" + extension;
            String saveFolder = new File("src/main/resources/static/img/profile").getAbsolutePath();
            String savePath = saveFolder + File.separator + saveName;

            try {
                image.transferTo(new File(savePath));
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }

            profileImgRepository.deleteByProfile(profile);

            ProfileImg profileImage = ProfileImg.builder()
                    .profile(profile)
                    .originalName(originalName)
                    .saveName(saveName)
                    .build();

            profileImgRepository.save(profileImage);
        }
    }

    // 조회
    @Transactional(readOnly = true)
    public ProfileResponseDTO getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUser_UserId(user.getUserId()).orElse(null);
        ProfileImg img = null;

        if (profile == null) {
            return ProfileResponseDTO.builder()
                    .penName(generateDefaultPenName())
                    .biography("")
                    .isPrivate(PrivateStatus.PUBLIC.name())
                    .imageUrl(null)
                    .build();
        }

        img = profileImgRepository.findByProfile(profile).orElse(null);

        return ProfileResponseDTO.builder()
                .profileId(profile.getProfileId())
                .penName(profile.getPenName())
                .biography(profile.getBiography())
                .isPrivate(profile.getIsPrivate().name())
                .imageUrl(img != null ? "/img/profile/" + img.getSaveName() : null)
                .build();
    }

    // 전체 삭제
    @Transactional
    public void deleteProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 없습니다."));

        deleteProfileImageFile(profile);
        profileRepository.delete(profile);
    }

    // 이미지 단독 삭제
    @Transactional
    public void deleteProfileImage(String userId) {
        System.out.println(" [deleteProfileImage] 호출됨 - userId: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.out.println("사용자 없음");
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> {
                    System.out.println("프로필 없음");
                    return new IllegalArgumentException("프로필이 없습니다.");
                });

        ProfileImg img = profileImgRepository.findByProfile(profile).orElse(null);
        if (img == null) {
            System.out.println("삭제할 이미지 없음");
            return;
        }

        System.out.println("이미지 엔티티 있음: " + img.getSaveName());

        profileImgRepository.delete(img);

        // 실제 파일 삭제
        String path = "src/main/resources/static/img/profile/" + img.getSaveName();
        File file = new File(path);
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println(deleted ? " 파일 삭제 성공" : "파일 삭제 실패");
        } else {
            System.out.println(" 파일 존재하지 않음");
        }
    }


    // 실제 이미지 파일 및 DB 레코드 삭제 (공통 로직)
    private void deleteProfileImageFile(Profile profile) {
        ProfileImg img = profileImgRepository.findByProfile(profile).orElse(null);
        if (img == null) {
            System.out.println("삭제할 이미지가 없습니다.");
            return;
        }

        profileImgRepository.delete(img);

        String path = "src/main/resources/static/img/profile/" + img.getSaveName();
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("이미지 파일 삭제 성공");
            } else {
                System.out.println("이미지 파일 삭제 실패");
            }
        }
    }

    // 기본 필명 생성
    private String generateDefaultPenName() {
        int suffix = 1;
        String base = "Readio 기본 필명 ";
        while (profileRepository.existsByPenName(base + suffix)) {
            suffix++;
        }
        return base + suffix;
    }
}
