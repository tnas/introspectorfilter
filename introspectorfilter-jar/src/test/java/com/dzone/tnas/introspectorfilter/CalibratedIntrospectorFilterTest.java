package com.dzone.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dzone.tnas.introspectorfilter.util.PostFactory;
import com.dzone.tnas.introspectorfilter.model.Post;

class CalibratedIntrospectorFilterTest {

    private static List<Post> postsCollection;

    @BeforeAll
    public static void setUp() {
        postsCollection = PostFactory.generateList(10, 3, 2, new Random(2024));
    }

    @Test
    void shouldNotFilterTextInPublicationsWithHeight0() {
        var filter = new IntrospectorFilter(0, 10);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).toList();
        assertTrue(filteredList.isEmpty());
    }

    @Test
    void shouldFilterTextInPublicationsWithHeight1() {
        var filter = new IntrospectorFilter(1, 10);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).toList();
        assertEquals(2, filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(6, filteredList.getLast().getId());
    }

    @Test
    void shouldNotFilterByHashtagsWithWidth0() {
        var filter = new IntrospectorFilter(10, 0);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).toList();
        assertTrue(filteredList.isEmpty());
    }

    @Test
    void shouldFilterByHashtagsWithWidth1() {
        var filter = new IntrospectorFilter(0, 1);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(7, filteredList.getFirst().getId());
        assertEquals("Mapin Publishing", filteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldNotFilterByAddressWithBreadth1() {
        var filter = new IntrospectorFilter(2, 1);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Moldova")).toList();
        assertTrue(filteredList.isEmpty());
    }
    
    @Test
    void shouldFilterByAddressWithBreadth2() {
        var filter = new IntrospectorFilter(0, 2);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Moldova")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(6, filteredList.getFirst().getId());
        assertEquals("Keshashire", filteredList.getFirst().getAuthor().getAddress().getCity());
    }
}
