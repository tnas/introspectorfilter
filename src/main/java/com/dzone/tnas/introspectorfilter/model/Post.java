package com.dzone.tnas.introspectorfilter.model;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;

import java.util.List;

public class Post extends Publication {

    private long id;

    @Filterable
    private List<String> hashtags;

    @Filterable
    private List<Comment> comments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
