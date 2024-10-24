package io.github.tnas.introspectorfilter.util;

import com.github.javafaker.Faker;

import io.github.tnas.introspectorfilter.model.Address;
import io.github.tnas.introspectorfilter.model.Author;
import io.github.tnas.introspectorfilter.model.Comment;
import io.github.tnas.introspectorfilter.model.Post;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PostFactory {

	private PostFactory() { }
	
	public static List<Post> generateList(int size, int commentsPerPost, int numHashtags, Random random) {
		
		var faker = new Faker(random);
		
		return IntStream.range(0, size)
				.mapToObj(i -> {
					
					var comments = IntStream.range(0, commentsPerPost)
							.mapToObj(j -> new Comment((long) i * commentsPerPost + j, faker.book().publisher()))
							.collect(Collectors.toList());
					var hashtags = IntStream.rangeClosed(1, numHashtags)
							.mapToObj(k -> faker.ancient().hero()).collect(Collectors.toList());
					
					return new Post(i, hashtags, comments, 
							new Author(i, faker.name().name(), 
									new Address(i, faker.address().country(), faker.address().city(), faker.address().streetName())),
							faker.lorem().paragraph(), faker.number().randomDigit());
				}).collect(Collectors.toList());
	}
	
	public static void printList(List<Post> postsCollection) {
		postsCollection.forEach(System.out::println);
	}
}
