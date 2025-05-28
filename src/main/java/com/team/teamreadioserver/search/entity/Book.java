package com.team.teamreadioserver.search.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;

import java.time.LocalDate;


@Getter
//@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "book")
public class Book {

    @Id
    @Column(name = "book_isbn", length = 15)
    private String bookIsbn;

    @Column(name = "book_title", length = 200)
    private String bookTitle;

    @Lob
    @Column(name = "book_author", columnDefinition = "TEXT")
    private String bookAuthor;

    @Column(name = "book_publisher", length = 50)
    private String bookPublisher;

    @Lob
    @Column(name = "book_cover", columnDefinition = "TEXT")
    private String bookCover;

    @Lob
    @Column(name = "book_description", columnDefinition = "TEXT")
    private String bookDescription;

    @Column(name = "book_pubdate")
    private LocalDate bookPubdate;


}
