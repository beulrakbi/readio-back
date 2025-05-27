package com.team.teamreadioserver.search.dto;

public class BookRequestDTO {

    private String query;

    public BookRequestDTO() {}

    public BookRequestDTO(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
