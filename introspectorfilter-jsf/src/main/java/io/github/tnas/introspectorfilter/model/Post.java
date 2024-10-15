package io.github.tnas.introspectorfilter.model;

import java.util.List;

import io.github.tnas.introspectorfilter.annotation.Filterable;

public class Post extends Publication {

    private long id;

    @Filterable
    private List<String> hashtags;

    @Filterable
    private List<Comment> comments;
    
    @Filterable
    private Author author;

    public Post(long id, List<String> hashtags, List<Comment> comments, Author author, String text, int relevance) {
		this.id = id;
		this.hashtags = hashtags;
		this.comments = comments;
		this.author = author;
		this.setText(text);
		this.setRelevance(relevance);
	}

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

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", hashtags=" + hashtags + ", comments=" + comments +
				", author = " + author + ", relevance = " + getRelevance() + ", text = " + getText() + "]";
	}
    
    
}
