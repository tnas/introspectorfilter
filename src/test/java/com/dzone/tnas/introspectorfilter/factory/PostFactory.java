package com.dzone.tnas.introspectorfilter.factory;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.dzone.tnas.introspectorfilter.model.Address;
import com.dzone.tnas.introspectorfilter.model.Author;
import com.dzone.tnas.introspectorfilter.model.Comment;
import com.dzone.tnas.introspectorfilter.model.Post;
import com.github.javafaker.Faker;

public class PostFactory {

	private PostFactory() { }
	
	public static List<Post> generateList(int size, int commentsPerPost, int numHashtags, Random random) {
		
		var faker = new Faker(random);
		
		return IntStream.range(0, size)
				.mapToObj(i -> {
					
					var comments = IntStream.range(0, commentsPerPost)
							.mapToObj(j -> new Comment(i * commentsPerPost + j, faker.book().publisher()))
							.toList();
					var hashtags = IntStream.rangeClosed(1, numHashtags)
							.mapToObj(k -> faker.ancient().hero()).toList();
					
					return new Post(i, hashtags, comments, 
							new Author(i, faker.name().name(), 
									new Address(i, faker.address().country(), faker.address().city(), faker.address().streetName())),
							faker.lorem().paragraph(), faker.number().randomDigit());
				}).toList();
	}
	
	public static void printList(List<Post> postsCollection) {
		postsCollection.stream().forEach(System.out::println);
	}
}
