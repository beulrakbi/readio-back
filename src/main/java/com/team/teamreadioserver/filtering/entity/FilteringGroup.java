package com.team.teamreadioserver.filtering.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(name = "filtering_group")
@Getter
@ToString
@AllArgsConstructor
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

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "is_active")
    private String isActive;

    public FilteringGroup(String title, String content)
    {
        this.title = title;
        this.content = content;
        this.createAt = new Date();
        this.isActive = "N";
    }

    public void modifyFilteringGroupActiveState()
    {
        if(this.isActive.equals("Y"))
            this.isActive = "N";
        else
            this.isActive = "Y";
    }

    public void modifyFilteringGroup(String newTitle, String newContent)
    {
        this.title = newTitle;
        this.content = newContent;
    }

}
