package com.team.teamreadioserver.notice.entity;

import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // 이 부분을 추가 (noticeView setter 위함)
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("notice"))
@Getter
@Setter // 클래스 레벨에 Setter 추가 (noticeView setter 제거하고 이것 사용)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("notice_id"))
    private Integer noticeId;

    @Column(name = ("notice_title"))
    private String noticeTitle;

    @Column(name = ("notice_create_at"))
    private LocalDateTime noticeCreateAt;

    @Column(name = ("notice_view"))
    private int noticeView;

    @Column(name = ("notice_content"))
    private String noticeContent;

    @Column(name = ("notice_state"))
    @Enumerated(EnumType.STRING)
    private NoticeState noticeState;

    @OneToOne(mappedBy = "notice", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) // orphanRemoval = true 추가
    private NoticeImg noticeImg;

    @Column(name = ("user_id"))
    private String userId;

    @PrePersist
    public void prePersist() {
        if(this.noticeCreateAt == null) {
            this.noticeCreateAt = LocalDateTime.now();
        }
        if(this.userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                this.userId = authentication.getName();
            } else {
                // 이 부분은 보안상 인증된 사용자만 공지사항을 등록할 수 있도록 하는 것이 일반적입니다.
                // 임시로 "관리자" 또는 특정 사용자 ID를 할당하거나, 이 기능을 관리자만 사용할 수 있도록 필터링해야 합니다.
                this.userId = "admin"; // 테스트를 위해 임시로 'admin' 할당
                // throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다. 공지사항 작성은 로그인 후 가능합니다.");
            }
        }
    }

    // Notice 엔티티의 update 메소드 수정
    public void update(String title, String content, NoticeState state, NoticeImg newImg) {
        this.noticeTitle = title;
        this.noticeContent = content;
        this.noticeState = state;
        // setNoticeImg 메소드를 통해 이미지 연결 및 이전 이미지 처리 (orphanRemoval = true 사용)
        this.setNoticeImg(newImg);
    }

    public void setNoticeImg(NoticeImg img) {
        // 기존 이미지가 있고 새로운 이미지가 null이면 기존 이미지의 notice 참조를 끊어 orphanRemoval이 작동하게 함
        if (this.noticeImg != null && img == null) {
            this.noticeImg.setNotice(null);
        }
        this.noticeImg = img;
        if (img != null) {
            img.setNotice(this); // 새로운 이미지에 Notice 연결
        }
    }
}