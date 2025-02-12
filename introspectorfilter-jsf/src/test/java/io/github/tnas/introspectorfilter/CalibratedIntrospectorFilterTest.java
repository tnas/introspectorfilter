package io.github.tnas.introspectorfilter;

import io.github.tnas.introspectorfilter.model.Post;
import io.github.tnas.introspectorfilter.util.PostFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalibratedIntrospectorFilterTest {

    private static List<Post> postsCollection;

    @BeforeAll
    public static void setUp() {
        postsCollection = PostFactory.generateList(10, 3, 2, new Random(2024));
    }

    @Test
    void shouldNotFilterTextInPublicationsWithHeight0() {
        var filter = IntrospectorFilter.builder().withHeightBound(0).withBreadthBound(10).build();
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).toList();
        assertTrue(filteredList.isEmpty());
    }

    @Test
    void shouldFilterTextInPublicationsWithHeight1() {
        var filter = IntrospectorFilter.builder().withHeightBound(1).withBreadthBound(10).build();
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).toList();
        assertEquals(2, filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(6, filteredList.getLast().getId());
    }

    @Test
    void shouldNotFilterByHashtagsWithWidth0() {
        var filter = IntrospectorFilter.builder().withHeightBound(10).withBreadthBound(0).build();
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).toList();
        assertTrue(filteredList.isEmpty());
    }

    @Test
    void shouldFilterByHashtagsWithWidth1() {
        var filter = IntrospectorFilter.builder().withHeightBound(0).withBreadthBound(1).build();
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(7, filteredList.getFirst().getId());
        assertEquals("Mapin Publishing", filteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldNotFilterByAddressWithBreadth1() {
        var filter = IntrospectorFilter.builder().withHeightBound(2).withBreadthBound(1).build();
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Moldova")).toList();
        assertTrue(filteredList.isEmpty());
    }
    
    @Test
    void shouldFilterByAddressWithBreadth2() {
        var filter = IntrospectorFilter.builder().withHeightBound(0).withBreadthBound(2).build();
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Moldova")).toList();
        assertEquals(1, filteredList.size());
        assertEquals(6, filteredList.getFirst().getId());
        assertEquals("Keshashire", filteredList.getFirst().getAuthor().getAddress().getCity());
    }
}
