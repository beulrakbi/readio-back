package com.team.teamreadioserver.filtering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "filtering_group")
@Getter
@ToString
@RequiredArgsConstructor
public class FilteringGroup {

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

}
