package com.team.teamreadioserver.bookmark.entity;

import com.team.teamreadioserver.search.entity.Book; // <-- Book 엔티티 경로 수정
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Lombok @Setter 추가 (JPA 동작을 위해 필요)

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmark_book") // 실제 테이블명과 일치하는지 확인
@Getter
@Setter
public class BookBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Integer bookmarkId;

    // **Book 엔티티와의 ManyToOne 관계 설정**
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn",      // book_bookmark 테이블의 외래 키 컬럼명
            referencedColumnName = "book_isbn", // Book 테이블의 참조 대상 컬럼명
            nullable = false)
    private Book book; // Book 엔티티 객체 필드

    @Column(name = "user_id", nullable = false)
    private String userId;
}