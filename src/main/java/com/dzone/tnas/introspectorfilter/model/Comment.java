package com.dzone.tnas.introspectorfilter.model;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;

public class Comment {

    private long id;

    @Filterable
    private String review;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
