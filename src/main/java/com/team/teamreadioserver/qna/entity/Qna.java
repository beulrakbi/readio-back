package com.team.teamreadioserver.qna.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("qna"))
@Getter
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("qna_id"))
    private Integer qnaId;
    @Column(name = ("qna_title"))
    private String qnaTitle;
    @Column(name = ("qna_create_at"))
    private LocalDateTime qnaCreateAt;
    @Column(name = ("qna_view"))
    private int qnaView;
    @Column(name = ("qna_question"))
    private String qnaQuestion;
    @Column(name = ("qna_answer"))
    private String qnaAnswer;
    @Column(name = ("user_id"))
    private String userId;

    @PrePersist
    public void prePersist() {
        if (this.qnaCreateAt == null) { // 혹시라도 이미 설정된 값이 없으면
            this.qnaCreateAt = LocalDateTime.now();
        }
        if (this.userId == null) { // 혹시라도 이미 설정된 값이 없으면
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                this.userId = authentication.getName(); // 로그인한 아이디가 들어감
            } else {
                throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다. FAQ 작성은 로그인 후 가능합니다.");
            }
        }
    }


    public void updateQuestion(String title, String question) {
        this.qnaTitle = title;
        this.qnaQuestion = question;
    }
    public void createAnswer(String Answer){
        this.qnaAnswer = Answer;
    }

}
