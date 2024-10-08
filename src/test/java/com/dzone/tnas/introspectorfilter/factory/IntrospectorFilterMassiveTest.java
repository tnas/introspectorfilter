package com.dzone.tnas.introspectorfilter.factory;

import com.dzone.tnas.introspectorfilter.IntrospectorFilter;
import com.dzone.tnas.introspectorfilter.model.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;

class IntrospectorFilterMassiveTest {

    private static IntrospectorFilter filter;
    private static List<Post> postsCollection;

    @BeforeAll
    public static void setUp() {
        filter = new IntrospectorFilter();
        postsCollection = PostFactory.generateList(1000, 3000, 2000, new Random(4098));
    }

    @Test
    void shouldFilterBySomeString() {
        var filteredList = postsCollection.stream().filter(p -> filter.filter(p, "vitae")).toList();
        assertFalse(filteredList.isEmpty());
        System.out.println(filteredList.size());
    }
}
