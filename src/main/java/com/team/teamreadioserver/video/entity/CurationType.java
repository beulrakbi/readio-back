package com.team.teamreadioserver.video.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "curation_type")
@Getter
@ToString
@RequiredArgsConstructor
public class CurationType {
    @Id
    @Column(name = "type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int typeId;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "type_text")
    private String typeText;

    public void modifyTypeText(String typeText) {
        this.typeText = typeText;
    }
}
