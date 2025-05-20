package com.team.teamreadioserver.qna.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        this.userId = "test2";
        this.qnaCreateAt = LocalDateTime.now();
        this.qnaView = 0;
    }
    public void updateQuestion(String title, String question) {
        this.qnaTitle = title;
        this.qnaQuestion = question;
    }

}
