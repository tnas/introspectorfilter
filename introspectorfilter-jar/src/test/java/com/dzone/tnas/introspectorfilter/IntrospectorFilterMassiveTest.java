package com.dzone.tnas.introspectorfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dzone.tnas.introspectorfilter.util.PostFactory;
import com.dzone.tnas.introspectorfilter.model.Post;

class IntrospectorFilterMassiveTest {

    private static IntrospectorFilter filter;
    private static List<Post> postsCollection;

    @BeforeAll
    public static void setUp() {
        filter = new IntrospectorFilter();
        postsCollection = PostFactory.generateList(1000, 3000, 2000, new Random(4098));
    }

    @Test
    void shouldFilterBySomeKnownString() {
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).toList();
        assertFalse(filteredList.isEmpty());
        assertEquals(75, filteredList.size());
    }
}
