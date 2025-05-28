package com.team.teamreadioserver.bookmark.entity;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("bookmark_book"))
@Getter
public class BookBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("bookmark_id"))
    private Integer bookmarkId;

    @Column(name = ("book_isbn"))
    private String bookIsbn;

    @Column(name = ("user_id"))
    private String userId;
}
