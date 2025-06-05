package com.team.teamreadioserver.notice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더가 사용할 모든 필드를 포함하는 private 생성자
@Builder // 클래스 레벨에 Builder 추가
@Entity
@Table(name = ("notice_img"))
@Getter
@Setter
public class NoticeImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("img_id"))
    private Long imgId;

    @Column(name = ("original_name"))
    private String originalName;

    @Column(name = ("saved_name"))
    private String savedName;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;
}