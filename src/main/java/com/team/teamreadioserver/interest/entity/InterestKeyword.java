package com.team.teamreadioserver.interest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Entity
@Table(name="interest_keyword")
public class InterestKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_keyword_id", updatable = false, nullable = false)
    private Long interestKeywordId;


    @Column(name = "interest_keyword", nullable = false, unique = true)
    private String interestKeyword;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


}
