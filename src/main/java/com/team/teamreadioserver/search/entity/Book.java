package com.team.teamreadioserver.search.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "book")
public class Book {

    @Id
    private String bookIsbn;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookCover;
    private String bookDescription;
    private String bookPubDate;


}
