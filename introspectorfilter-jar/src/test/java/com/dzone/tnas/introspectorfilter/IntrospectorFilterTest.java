package com.dzone.tnas.introspectorfilter;

import com.dzone.tnas.introspectorfilter.factory.PostFactory;
import com.dzone.tnas.introspectorfilter.model.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntrospectorFilterTest {

    private static IntrospectorFilter filter;
    private static List<Post> postsCollection;

    @BeforeAll
    public static void setUp() {
        filter = new IntrospectorFilter();
        postsCollection = PostFactory.generateList(10, 3, 2, new Random(2024));
    }

    @Test
    void shouldFilterByTextInPublications() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).toList();
        assertEquals(2, filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(6, filteredList.getLast().getId());
    }
    
    @Test
    void shouldFilterByHashtags() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(7, filteredList.getFirst().getId());
        assertEquals("Mapin Publishing", filteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldFilterByReviewInComments() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Harlequin")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(3, filteredList.getFirst().getId());
        assertEquals("United States Government Publishing Office", filteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldFilterByIntegerRelevanceInPublications() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, 6)).toList();
        assertEquals(2, filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(4, filteredList.getLast().getId());
    }
    
    @Test
    void shouldFilterByAuthorName() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Berge")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals("Amee Berge", filteredList.getFirst().getAuthor().getName());
    }
    
    @Test
    void shouldFilterByCountryInAddress() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Kyrgyz Republic")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(8, filteredList.getFirst().getId());
        assertEquals("East Adrian", filteredList.getFirst().getAuthor().getAddress().getCity());
    }
    
    @Test
    void shouldFilterByLocationDescription() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Greens")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(8, filteredList.getFirst().getId());
        assertEquals("Valentine Greens", filteredList.getFirst().getAuthor().getAddress().getDescription());
    }
    
    @Test
    void shouldNotFilterIntegerIDNotFilterable() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, 9)).toList();
    	assertTrue(filteredList.isEmpty());
    }
}
