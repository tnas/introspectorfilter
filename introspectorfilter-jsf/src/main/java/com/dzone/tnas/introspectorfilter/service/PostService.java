package com.dzone.tnas.introspectorfilter.service;

import com.dzone.tnas.introspectorfilter.model.Post;
import com.dzone.tnas.introspectorfilter.util.PostFactory;

import java.util.List;
import java.util.Random;

public class PostService {

    private static final PostService instance = new PostService();

    public static PostService getInstance() {
        return instance;
    }

    private PostService() { }

    public List<Post> getPosts() {
        return PostFactory.generateList(50, 5, 3, new Random(1024));
    }
}
