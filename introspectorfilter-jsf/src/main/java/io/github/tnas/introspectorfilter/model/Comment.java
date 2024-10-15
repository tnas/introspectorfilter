package io.github.tnas.introspectorfilter.model;

import io.github.tnas.introspectorfilter.annotation.Filterable;

public class Comment {

    private long id;

    @Filterable
    private String review;

    public Comment(long id, String review) {
		this.id = id;
		this.review = review;
	}

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

	@Override
	public String toString() {
		return "Comment [id=" + id + ", review=" + review + "]";
	}
    
}
