package com.dzone.tnas.introspectorfilter.bean;

import com.dzone.tnas.introspectorfilter.model.Post;
import com.dzone.tnas.introspectorfilter.service.PostService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class PostBean {

    private List<Post> posts;
    private List<Post> filteredPosts;

    @PostConstruct
    public void init() {
        this.posts = PostService.getInstance().getPosts();
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getFilteredPosts() {
        return filteredPosts;
    }

    public void setFilteredPosts(List<Post> filteredPosts) {
        this.filteredPosts = filteredPosts;
    }
}
