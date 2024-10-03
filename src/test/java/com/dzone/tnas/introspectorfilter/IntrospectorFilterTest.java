package com.dzone.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dzone.tnas.introspectorfilter.factory.PostFactory;
import com.dzone.tnas.introspectorfilter.model.Post;

class IntrospectorFilterTest {

    private IntrospectorFilter<Post> filter;
    private List<Post> postsCollection;

    @BeforeEach
    public void setUp() {
        this.filter = new IntrospectorFilter<>();
        this.postsCollection = PostFactory.generateList(10, 3, 2);
    }

    @Test
    void shouldFilterByHashtag() {
    	var filteredList = this.postsCollection.stream().filter(p -> this.filter.filter(p, "Thrace")).toList();
        assertEquals(1, filteredList.size());
//        assertEquals(7, filteredList.getFirst().getId());
//        assertEquals("George Newnes", filteredList.getFirst().getComments().getFirst().getReview());
    }
}
