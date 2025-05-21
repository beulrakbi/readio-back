package com.team.teamreadioserver.video.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "curation_keywords")
@Getter
@ToString
@RequiredArgsConstructor
public class CurationKeywords {

    @Id
    @Column(name = "curation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int curationId;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "type")
    private String type;
}
