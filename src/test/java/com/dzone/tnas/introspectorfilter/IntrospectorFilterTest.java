package com.dzone.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

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
//        PostFactory.printList(postsCollection);
    }

    @Test
    void shouldFilterByTextInPublications() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "tenetur")).toList();
        assertEquals(3, filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(6, filteredList.getLast().getId());
    }
    
    @Test
    void shouldFilterByHashtags() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Iphigenia")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(7, filteredList.getFirst().getId());
        assertEquals("Cloverdale Corporation", filteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldFilterByReviewInComments() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Appleton")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(3, filteredList.getFirst().getId());
        assertEquals("Hawthorne Books", filteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldFilterByIntegerRelevanceInPublications() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, 8)).toList();
        assertEquals(2, filteredList.size());
        assertEquals(2, filteredList.getFirst().getId());
        assertEquals(3, filteredList.getLast().getId());
    }
    
    @Test
    void shouldFilterByAuthorName() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Ruecker")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(4, filteredList.getFirst().getId());
        assertEquals("Dusty Ruecker", filteredList.getFirst().getAuthor().getName());
    }
    
    @Test
    void shouldFilterByCountryInAddress() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Albania")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(8, filteredList.getFirst().getId());
        assertEquals("Turcotteville", filteredList.getFirst().getAuthor().getAddress().getCity());
    }
    
    @Test
    void shouldNotFilterIntegerIDNotFilterable() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, 9)).toList();
    	assertTrue(filteredList.isEmpty());
    }
}
