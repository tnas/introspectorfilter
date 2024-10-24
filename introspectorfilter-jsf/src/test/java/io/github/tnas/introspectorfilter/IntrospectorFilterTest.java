package io.github.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, textFilter)).collect(Collectors.toList());
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(1, filteredList.get(0).getId());
        assertEquals(6, filteredList.get(filteredList.size() - 1).getId());
        assertEquals(1, traditionalFilteredList.get(0).getId());
        assertEquals(6, traditionalFilteredList.get(traditionalFilteredList.size() - 1).getId());
    }
    
    @Test
    void shouldFilterByHashtags() {

        var textFilter = "HECUBA";

        var traditionalFilteredList = postsCollection.stream()
                .filter(p -> Objects.isNull(p.getHashtags()) || p.getHashtags().isEmpty() ||
                        p.getHashtags().stream().anyMatch(h ->
                                StringUtils.stripAccents(h.trim().toLowerCase())
                                        .contains(textFilter.trim().toLowerCase())))
                .collect(Collectors.toList());

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Hecuba")).collect(Collectors.toList());
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(7, filteredList.get(0).getId());
        assertEquals("Mapin Publishing", filteredList.get(0).getComments().get(0).getReview());

        assertEquals(7, traditionalFilteredList.get(0).getId());
        assertEquals("Mapin Publishing", traditionalFilteredList.get(0).getComments().get(0).getReview());
    }
    
    @Test
    void shouldFilterByReviewInComments() {

        var textFilter = "Harlequin";

        var traditionalFilteredList = postsCollection.stream()
                .filter(p -> Objects.isNull(p.getComments()) || p.getComments().isEmpty() ||
                        p.getComments().stream().anyMatch(c ->
                                StringUtils.stripAccents(c.getReview().trim().toLowerCase())
                                        .contains(textFilter.trim().toLowerCase())))
                .collect(Collectors.toList());

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Harlequin")).collect(Collectors.toList());
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(3, filteredList.get(0).getId());
        assertEquals("United States Government Publishing Office",
                filteredList.get(0).getComments().get(0).getReview());

        assertEquals(3, traditionalFilteredList.get(0).getId());
        assertEquals("United States Government Publishing Office",
                traditionalFilteredList.get(0).getComments().get(0).getReview());
    }
    
    @Test
    void shouldFilterByIntegerRelevanceInPublications() {

        var textFilter = 6;

        var traditionalFilteredList = postsCollection.stream()
                .filter(p -> p.getRelevance() == textFilter)
                .collect(Collectors.toList());

    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, 6)).collect(Collectors.toList());
        assertEquals(traditionalFilteredList.size(), filteredList.size());
        assertEquals(1, filteredList.get(0).getId());
        assertEquals(4, filteredList.get(filteredList.size() - 1).getId());

        assertEquals(1, traditionalFilteredList.get(0).getId());
        assertEquals(4, traditionalFilteredList.get(traditionalFilteredList.size() - 1).getId());
    }
    
    @Test
    void shouldFilterByAuthorName() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Berge")).collect(Collectors.toList());
        assertEquals(1, filteredList.size());
        assertEquals(1, filteredList.get(0).getId());
        assertEquals("Amee Berge", filteredList.get(0).getAuthor().getName());
    }
    
    @Test
    void shouldFilterByCountryInAddress() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Kyrgyz Republic")).collect(Collectors.toList());
        assertEquals(1, filteredList.size());
        assertEquals(8, filteredList.get(0).getId());
        assertEquals("East Adrian", filteredList.get(0).getAuthor().getAddress().getCity());
    }
    
    @Test
    void shouldFilterByLocationDescription() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "Greens")).collect(Collectors.toList());
        assertEquals(1, filteredList.size());
        assertEquals(8, filteredList.get(0).getId());
        assertEquals("Valentine Greens", filteredList.get(0).getAuthor().getAddress().getDescription());
    }
    
    @Test
    void shouldNotFilterIntegerIDNotFilterable() {
    	var filteredList = postsCollection.stream().filter(p -> filter.filter(p, 9)).collect(Collectors.toList());
    	assertTrue(filteredList.isEmpty());
    }
}
