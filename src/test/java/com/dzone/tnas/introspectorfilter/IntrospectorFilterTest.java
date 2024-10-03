package com.dzone.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dzone.tnas.introspectorfilter.factory.PostFactory;
import com.dzone.tnas.introspectorfilter.model.Post;

class IntrospectorFilterTest {

    private static IntrospectorFilter<Post> filter;
    private static List<Post> postsCollection;

    @BeforeAll
    public static void setUp() {
        filter = new IntrospectorFilter<>();
        postsCollection = PostFactory.generateList(10, 3, 2, new Random(2024));
    }

    @Test
    void shouldFilterByTextInPublication() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "quis")).toList();
        assertEquals(2, filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(5, filteredList.getLast().getId());
    }
    
    @Test
    void shouldFilterByHashtags() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Andromeda")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(7, filteredList.getFirst().getId());
        assertEquals("George Newnes", filteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldFilterByReviewInComments() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Packt")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(3, filteredList.getFirst().getId());
        assertEquals("McFarland & Company", filteredList.getFirst().getComments().getFirst().getReview());
        assertTrue(ClassUtils.isPrimitiveWrapper(Integer.class));
    }
}
