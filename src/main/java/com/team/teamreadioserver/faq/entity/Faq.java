package com.team.teamreadioserver.faq.entity;

import com.team.teamreadioserver.notice.entity.NoticeImg;
import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @PrePersist
    public void prePersist() {
        this.userId = "test2";
        this.faqCreateAt = LocalDateTime.now();
    }

    public void updateFaq(String title, String content) {
        this.faqTitle = title;
        this.faqContent = content;
    }
}
