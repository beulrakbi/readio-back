package com.team.teamreadioserver.notice.entity;

import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("notice"))
@Getter
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

    @OneToOne(mappedBy = "notice", fetch = FetchType.LAZY, cascade = CascadeType.ALL) //fetch 노란줄 무시해도 괜찮음
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
                throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다. 공지사항 작성은 로그인 후 가능합니다.");
            }
        }
    }

    public void update(String title, String content, NoticeState state, NoticeImg img) {
        this.noticeTitle = title;
        this.noticeContent = content;
        this.noticeState = state;
        this.setNoticeImg(img); // ✅ 이렇게 써야 연결됨
    }

    public void setNoticeImg(NoticeImg img) {
        this.noticeImg = img;
        if (img != null) {
            img.setNotice(this);
        }
    }

    // ✨ 조회수 증가를 위한 setter 추가
    public void setNoticeView(int noticeView) {
        this.noticeView = noticeView;
    }
}