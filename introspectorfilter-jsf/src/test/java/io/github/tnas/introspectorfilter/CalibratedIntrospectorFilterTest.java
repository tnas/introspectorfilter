package io.github.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.tnas.introspectorfilter.model.Post;
import io.github.tnas.introspectorfilter.util.PostFactory;

class CalibratedIntrospectorFilterTest {

    private static List<Post> postsCollection;

    @BeforeAll
    public static void setUp() {
        postsCollection = PostFactory.generateList(10, 3, 2, new Random(2024));
    }

    @Test
    void shouldNotFilterTextInPublicationsWithHeight0() {
        var filter = new IntrospectorFilter(0, 10);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).collect(Collectors.toList());
        assertTrue(filteredList.isEmpty());
    }

    @Test
    void shouldFilterTextInPublicationsWithHeight1() {
        var filter = new IntrospectorFilter(1, 10);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).collect(Collectors.toList());
        assertEquals(2, filteredList.size());
        assertEquals(1, filteredList.get(0).getId());
        assertEquals(6, filteredList.get(filteredList.size() - 1).getId());
    }

    @Test
    void shouldNotFilterByHashtagsWithWidth0() {
        var filter = new IntrospectorFilter(10, 0);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).collect(Collectors.toList());
        assertTrue(filteredList.isEmpty());
    }

    @Test
    void shouldFilterByHashtagsWithWidth1() {
        var filter = new IntrospectorFilter(0, 1);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).collect(Collectors.toList());
        assertEquals(1, filteredList.size());
        assertEquals(7, filteredList.get(0).getId());
        assertEquals("Mapin Publishing", filteredList.get(0).getComments().get(0).getReview());
    }
    
    @Test
    void shouldNotFilterByAddressWithBreadth1() {
        var filter = new IntrospectorFilter(2, 1);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Moldova")).collect(Collectors.toList());
        assertTrue(filteredList.isEmpty());
    }
    
    @Test
    void shouldFilterByAddressWithBreadth2() {
        var filter = new IntrospectorFilter(0, 2);
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Moldova")).collect(Collectors.toList());
        assertEquals(1, filteredList.size());
        assertEquals(6, filteredList.get(0).getId());
        assertEquals("Keshashire", filteredList.get(0).getAuthor().getAddress().getCity());
    }
}
