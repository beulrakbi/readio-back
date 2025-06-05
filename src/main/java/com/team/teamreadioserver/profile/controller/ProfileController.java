package com.team.teamreadioserver.profile.controller;

import com.team.teamreadioserver.profile.dto.ProfileRequestDTO;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<?> upsertProfile(@ModelAttribute ProfileRequestDTO profileRequestDTO) {
        profileService.upsertProfile(profileRequestDTO);
        return ResponseEntity.ok("프로필이 저장되었습니다.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @DeleteMapping("/image/{userId}")
    public ResponseEntity<?> deleteProfileImage(@PathVariable("userId") String userId) {
        profileService.deleteProfileImage(userId);
        return ResponseEntity.ok("프로필 이미지가 삭제되었습니다.");
    }

    @GetMapping("/penname/check")
    public ResponseEntity<?> checkPenName(@RequestParam("value") String penName) {
        boolean available = !profileService.isPenNameTaken(penName);
        return ResponseEntity.ok().body(
                java.util.Collections.singletonMap("available", available)
        );
    }


}