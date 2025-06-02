package com.team.teamreadioserver.faq.entity;

import com.team.teamreadioserver.notice.entity.NoticeImg;
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
@Table(name = ("faq"))
@Getter
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("faq_id"))
    private Integer faqId;

    @Column(name = ("faq_title"))
    private String faqTitle;

    @Column(name = ("faq_content"))
    private String faqContent;

    @Column(name = ("faq_create_at"))
    private LocalDateTime faqCreateAt;

    @Column(name = ("user_id"))
    private String userId;

    // 하드코딩한 부분은 꼭 지우고 테스트하셔야해요~!!
    // 디버깅할때 계정 2개로 찍히면 혼돈 그자체
//    @PrePersist
//    public void prePersist() {
//        this.userId = "test2";
//        this.faqCreateAt = LocalDateTime.now();
//    }

    // 재용님 아래 @PrePersist 붙은 코드 추가하면 정상 등록돼요 (보경)
    // 하드코딩(test2) 주석처리하면서 @PrePersist 이게 붙은 코드가 사라져서 그랬나봐요
    // FaqService 클래스에서는 userId, faqCreateAt 를 @PrePersist 얘가 처리한다고 써놓으셨는데 막상 로직을 구현한 부분이 없었어요! (하드코딩말고!)
    // 그래서 두 값은 not null인데 null 값이 들어오면서 에러가 발생했던 것 같습니다... 
    //2025-05-30T01:38:47.015+09:00 TRACE 25700 --- [nio-8080-exec-4] org.hibernate.orm.jdbc.bind              : binding parameter (1:VARCHAR) <- [야호~~]
    //2025-05-30T01:38:47.015+09:00 TRACE 25700 --- [nio-8080-exec-4] org.hibernate.orm.jdbc.bind              : binding parameter (2:TIMESTAMP) <- [null]
    //2025-05-30T01:38:47.015+09:00 TRACE 25700 --- [nio-8080-exec-4] org.hibernate.orm.jdbc.bind              : binding parameter (3:VARCHAR) <- [재용님 이거 등록된다요~~~]
    //2025-05-30T01:38:47.015+09:00 TRACE 25700 --- [nio-8080-exec-4] org.hibernate.orm.jdbc.bind              : binding parameter (4:VARCHAR) <- [null]
    //2025-05-30T01:24:52.483+09:00  WARN 28972 --- [nio-8080-exec-3] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 1048, SQLState: 23000
    //2025-05-30T01:24:52.483+09:00 ERROR 28972 --- [nio-8080-exec-3] o.h.engine.jdbc.spi.SqlExceptionHelper   : Column 'faq_create_at' cannot be null

    @PrePersist
    public void prePersist() {
        // 1. faqCreateAt 설정: 현재 시간을 명시적으로 설정
        if (this.faqCreateAt == null) { // 혹시라도 이미 설정된 값이 없으면
            this.faqCreateAt = LocalDateTime.now();
        }

        // 2. userId 설정: SecurityContextHolder에서 현재 로그인된 사용자 ID 가져오기
        // 이전 'test2' 하드코딩 부분이었다면, 이 로직으로 대체
        if (this.userId == null) { // 혹시라도 이미 설정된 값이 없으면
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                this.userId = authentication.getName(); // 로그인한 아이디가 들어감
            } else {
                // 비로그인 사용자 또는 인증 정보가 없을 경우 처리 (선택 사항)
                // 예: this.userId = "anonymous";
                // 또는 예외를 던져서 명확한 에러를 알릴 수 있습니다.
                throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다. FAQ 작성은 로그인 후 가능합니다.");
            }
        }
    }

    public void updateFaq(String title, String content) {
        this.faqTitle = title;
        this.faqContent = content;
    }
}
