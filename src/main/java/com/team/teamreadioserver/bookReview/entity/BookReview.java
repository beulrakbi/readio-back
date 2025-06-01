package com.team.teamreadioserver.bookReview.entity;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import com.team.teamreadioserver.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Date;

import static com.team.teamreadioserver.bookReview.enumPackage.IsHidden.Y;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("book_review"))
@Getter
public class BookReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("review_id"))
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정 (선택 사항, 성능 고려)
    @JoinColumn(name = ("profile_id"), referencedColumnName = ("profile_id"), nullable = false)
    private Profile profile;

    @Column(name=("book_isbn"), nullable=false)
    private String bookIsbn;

    @Column(name = ("review_content"))
    private String reviewContent;

    @Column(name = ("reported_count"), nullable = false)
    private Integer reportedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = ("is_hidden"), nullable = false)
    private IsHidden isHidden;

    @Column(name = ("created_at"))
    private Date createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
        if (this.isHidden == null) {
            this.isHidden = IsHidden.N;
        }

        if (this.profile == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                String userId = authentication.getName();
                // 프로필은 서비스나 컨트롤러에서 주입하는게 안전,
                // 여기는 예외처리만 해도 좋음
                throw new IllegalStateException("Profile이 설정되지 않았습니다. 서비스에서 Profile을 주입해주세요. userId: " + userId);
            } else {
                throw new IllegalStateException("인증된 사용자 정보가 없습니다.");
            }
        }
    }
    public void report() {
        this.reportedCount++;
    }
    public void hide() {
        this.isHidden = IsHidden.Y;
    }
}