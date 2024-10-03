package com.dzone.tnas.introspectorfilter.factory;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.dzone.tnas.introspectorfilter.model.Comment;
import com.dzone.tnas.introspectorfilter.model.Post;
import com.github.javafaker.Faker;

public class PostFactory {

	private PostFactory() { }
	
	public static List<Post> generateList(int size, int commentsPerPost, int numHashtags) {
		
		var faker = new Faker(new Random(2024));
		
		return IntStream.range(0, size)
				.mapToObj(i -> {
					
					var comments = IntStream.range(0, commentsPerPost)
							.mapToObj(j -> new Comment(i * commentsPerPost + j, faker.book().publisher()))
							.toList();
					var hashtags = IntStream.rangeClosed(1, numHashtags)
							.mapToObj(k -> faker.ancient().hero()).toList();
					
					return new Post(i, hashtags, comments, faker.lorem().paragraph());
				}).toList();
	}
}
