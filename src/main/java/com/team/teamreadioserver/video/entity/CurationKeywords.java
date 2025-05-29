package com.team.teamreadioserver.video.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curation_keywords")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CurationKeywords {

    @Id
    @Column(name = "curation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int curationId;

    @EqualsAndHashCode.Include
    @Column(name = "keyword")
    private String keyword;

    @Column(name = "type_id")
    private int typeId;
}
