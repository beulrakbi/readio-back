package com.team.teamreadioserver.interest.entity;

import com.team.teamreadioserver.interest.enums.InterestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

//@Entity
//@Table(name="user_interest_keyword")
//public class UserInterestKeyword {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_interest_keyword_id", updatable = false, nullable = false)
//    private Long userInterestKeywordId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    private User userId;;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "interest_keyword_id", referencedColumnName = "interest_keyword_id")
//    @Column(name = "interest_keyword_id", nullable = false)
//    private Long interestId;

//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private InterestStatus status;
}
