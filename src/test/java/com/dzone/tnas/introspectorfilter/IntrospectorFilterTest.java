package com.dzone.tnas.introspectorfilter;

import com.dzone.tnas.introspectorfilter.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntrospectorFilterTest {

    private IntrospectorFilter<Post> filter;

    @BeforeEach
    public void setUp() {
        this.filter = new IntrospectorFilter<>();
    }

    @Test
    void shouldAnswerWithTrue() {
        this.filter.findStringsToFilter(new Post());
        assertTrue(true);
    }
}
