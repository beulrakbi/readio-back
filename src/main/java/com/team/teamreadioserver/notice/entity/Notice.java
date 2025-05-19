package com.team.teamreadioserver.notice.entity;

import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import io.swagger.v3.oas.annotations.info.Info;
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
    private Integer userId;

    @PrePersist
    public void prePersist() {
        this.userId = 1;
        this.noticeCreateAt = LocalDateTime.now();
        this.noticeView = 0;
    }

    public void update(String title, String content, NoticeState state, NoticeImg img) {
        this.noticeTitle = title;
        this.noticeContent = content;
        this.noticeState = state;
        this.noticeImg = img;
    }
}
