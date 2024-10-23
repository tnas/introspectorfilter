package io.github.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.tnas.introspectorfilter.model.Post;
import io.github.tnas.introspectorfilter.util.PostFactory;

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

        var textFilter = "vitae";

        var traditionalFilteredList = postsCollection.stream()
                .filter(p -> Objects.isNull(p.getText()) ||
                        StringUtils.stripAccents(p.getText().trim().toLowerCase()).contains(textFilter.trim().toLowerCase()))
                .toList();

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, textFilter)).toList();
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(6, filteredList.getLast().getId());
        assertEquals(1, traditionalFilteredList.getFirst().getId());
        assertEquals(6, traditionalFilteredList.getLast().getId());
    }
    
    @Test
    void shouldFilterByHashtags() {

        var textFilter = "HECUBA";

        var traditionalFilteredList = postsCollection.stream()
                .filter(p -> Objects.isNull(p.getHashtags()) || p.getHashtags().isEmpty() ||
                        p.getHashtags().stream().anyMatch(h ->
                                StringUtils.stripAccents(h.trim().toLowerCase())
                                        .contains(textFilter.trim().toLowerCase())))
                .toList();

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).toList();
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(7, filteredList.getFirst().getId());
        assertEquals("Mapin Publishing", filteredList.getFirst().getComments().getFirst().getReview());

        assertEquals(7, traditionalFilteredList.getFirst().getId());
        assertEquals("Mapin Publishing", traditionalFilteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldFilterByReviewInComments() {

        var textFilter = "Harlequin";

        var traditionalFilteredList = postsCollection.stream()
                .filter(p -> Objects.isNull(p.getComments()) || p.getComments().isEmpty() ||
                        p.getComments().stream().anyMatch(c ->
                                StringUtils.stripAccents(c.getReview().trim().toLowerCase())
                                        .contains(textFilter.trim().toLowerCase())))
                .toList();

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Harlequin")).toList();
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(3, filteredList.getFirst().getId());
        assertEquals("United States Government Publishing Office",
                filteredList.getFirst().getComments().getFirst().getReview());

        assertEquals(3, traditionalFilteredList.getFirst().getId());
        assertEquals("United States Government Publishing Office",
                traditionalFilteredList.getFirst().getComments().getFirst().getReview());
    }
    
    @Test
    void shouldFilterByIntegerRelevanceInPublications() {

        var textFilter = 6;

        var traditionalFilteredList = postsCollection.stream()
                .filter(p -> p.getRelevance() == textFilter)
                .toList();

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, 6)).toList();
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(1, filteredList.getFirst().getId());
        assertEquals(4, filteredList.getLast().getId());

        assertEquals(1, traditionalFilteredList.getFirst().getId());
        assertEquals(4, traditionalFilteredList.getLast().getId());
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
