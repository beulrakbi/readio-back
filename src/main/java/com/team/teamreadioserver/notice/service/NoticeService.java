package com.team.teamreadioserver.notice.service;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.dto.NoticeResponseDTO;
import com.team.teamreadioserver.notice.dto.NoticeUpdateDTO;
import com.team.teamreadioserver.notice.entity.Notice;
import com.team.teamreadioserver.notice.entity.NoticeImg;
import com.team.teamreadioserver.notice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    // ✅ 이미지 저장 경로:
    // 개발 환경에서는 이 경로를 사용하세요.
    // Mac/Linux 예: "/Users/hwangjaeyong/Desktop/readioBackServer/uploads/notice_images"
    // Windows 예: "C:/readioBackServer/uploads/notice_images"
    // 실제 운영 환경에서는 서버의 독립적인 경로 (예: /var/www/uploads/notice_images)를 설정해야 합니다.
    private final String IMAGE_UPLOAD_DIR = "/Users/hwangjaeyong/Desktop/readioBackServer/uploads/notice_images";


    public List<NoticeResponseDTO> getNoticeList() {
        List<Notice> notices = noticeRepository.findAllByOrderByNoticeCreateAtDesc(); // 최신순 정렬

        return notices.stream()
                .map(notice -> NoticeResponseDTO.fromEntity(notice)) // fromEntity 사용
                .collect(Collectors.toList());
    }

    @Transactional // writeNotice에 @Transactional 추가 (파일 저장, DB 저장 모두 포함되므로)
    public void writeNotice(NoticeRequestDTO requestDTO) {
        Notice notice = Notice.builder()
                .noticeTitle(requestDTO.getNoticeTitle())
                .noticeContent(requestDTO.getNoticeContent())
                .noticeState(requestDTO.getNoticeState())
                // userId는 @PrePersist에서 설정되므로 여기서 명시할 필요 없음
                .build();

        // 이미지 파일 처리
        NoticeImg noticeImg = saveNoticeImage(requestDTO.getNoticeImgFile()); // 파일 저장 메소드 호출
        if (noticeImg != null) {
            notice.setNoticeImg(noticeImg); // 엔티티에 연결
        }

        noticeRepository.save(notice);
    }

    @Transactional
    public void updateNotice(NoticeUpdateDTO updateDTO) {
        Notice notice = noticeRepository.findById(updateDTO.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다."));

        // 기존 이미지 파일 삭제 (새로운 이미지 있을 경우 또는 이미지 삭제 요청 시)
        // updateDTO.getNoticeImgFile()이 null이고, 기존 이미지가 있다면 삭제 요청으로 간주
        if (updateDTO.getNoticeImgFile() == null || updateDTO.getNoticeImgFile().isEmpty()) {
            if (notice.getNoticeImg() != null) {
                // 기존 파일이 있지만, 새로운 파일이 없거나 비어있는 경우 (즉, 이미지 삭제 요청)
                deleteNoticeImageFile(notice.getNoticeImg());
                notice.setNoticeImg(null); // DB에서도 연결 끊기 (orphanRemoval = true에 의해 삭제됨)
            }
        } else { // 새로운 이미지 파일이 전달된 경우
            if (notice.getNoticeImg() != null) {
                // 기존 이미지가 있다면 먼저 물리 파일 삭제
                deleteNoticeImageFile(notice.getNoticeImg());
            }
            // 새로운 이미지 파일 저장 및 연결
            NoticeImg newNoticeImg = saveNoticeImage(updateDTO.getNoticeImgFile());
            notice.setNoticeImg(newNoticeImg);
        }

        notice.update(
                updateDTO.getNoticeTitle(),
                updateDTO.getNoticeContent(),
                updateDTO.getNoticeState(),
                notice.getNoticeImg() // 이미 업데이트된 noticeImg 객체를 전달
        );
        // @Transactional 덕분에 noticeRepository.save(notice); 명시적 호출 필요 없음
    }


    @Transactional
    public void deleteNotice(Integer noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다."));

        // 연관된 이미지 파일 먼저 삭제
        deleteNoticeImageFile(notice.getNoticeImg());

        noticeRepository.delete(notice);
    }

    @Transactional // 트랜잭션 추가
    public NoticeResponseDTO detail(Integer noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        // 조회수 1 증가
        notice.setNoticeView(notice.getNoticeView() + 1);
        // save를 명시적으로 호출할 필요 없음: @Transactional 어노테이션이 붙은 메서드 내에서 엔티티의 상태가 변경되면
        // 트랜잭션이 커밋될 때 자동으로 변경사항이 데이터베이스에 반영됩니다 (Dirty Checking).

        // 이미지 URL 포함하여 응답 DTO 생성
        NoticeResponseDTO responseDTO = NoticeResponseDTO.fromEntity(notice);
        if (notice.getNoticeImg() != null) {
            // ✅ 이미지 URL을 웹에서 접근 가능한 경로로 설정
            // application.properties에 설정된 /notice_images/ 접두사를 사용합니다.
            responseDTO.setImageUrl("/notice_images/" + notice.getNoticeImg().getSavedName());
        }
        return responseDTO;
    }

    public List<NoticeResponseDTO> searchNoticesByTitle(String keyword) {
        List<Notice> notices = noticeRepository.findByNoticeTitleContainingIgnoreCase(keyword);

        return notices.stream()
                .map(NoticeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // --- 이미지 파일 저장 및 삭제 헬퍼 메소드 ---

    private NoticeImg saveNoticeImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        // 파일 저장 경로 설정
        File uploadDir = new File(IMAGE_UPLOAD_DIR);
        if (!uploadDir.exists()) {
            // mkdirs()는 필요한 상위 디렉토리도 모두 생성합니다.
            boolean created = uploadDir.mkdirs();
            if (!created) {
                // 디렉토리 생성 실패 시 에러 처리
                throw new RuntimeException("이미지 저장 디렉토리 생성 실패: " + IMAGE_UPLOAD_DIR);
            }
        }

        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 고유한 파일 이름 생성 (UUID 사용 권장)
        String savedFilename = UUID.randomUUID().toString() + extension;
        File targetFile = new File(uploadDir, savedFilename);

        try {
            imageFile.transferTo(targetFile);
            NoticeImg noticeImg = NoticeImg.builder()
                    .originalName(originalFilename)
                    .savedName(savedFilename)
                    .build();
            return noticeImg;
        } catch (IOException e) {
            throw new RuntimeException("공지사항 이미지 저장 실패: " + targetFile.getAbsolutePath(), e);
        }
    }

    private void deleteNoticeImageFile(NoticeImg noticeImg) {
        if (noticeImg == null || noticeImg.getSavedName() == null) {
            return;
        }
        // ✅ 삭제 경로도 동일하게 외부 경로를 사용해야 합니다.
        File file = new File(IMAGE_UPLOAD_DIR + File.separator + noticeImg.getSavedName());
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("파일 삭제 실패: " + file.getAbsolutePath());
                // 실제 애플리케이션에서는 로깅 라이브러리를 사용하여 에러 로그를 남기는 것이 좋습니다.
            }
        }
        // DB에서 NoticeImg 엔티티를 직접 삭제할 필요는 없습니다.
        // Notice 엔티티의 CascadeType.ALL 및 orphanRemoval = true 설정으로 Notice 삭제 시 NoticeImg도 함께 삭제되거나,
        // Notice.setNoticeImg(null)을 통해 연결을 끊으면 자동으로 삭제됩니다.
        // 여기서는 물리적 파일만 삭제하는 역할입니다.
    }
}