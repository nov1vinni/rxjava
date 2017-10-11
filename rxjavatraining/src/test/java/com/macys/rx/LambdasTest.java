package com.macys.rx;


import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import junit.framework.Assert;

import org.assertj.core.util.Lists;
import org.junit.Test;

/**http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/
 * 
 * @author m474523
 *
 */
public class LambdasTest {
	
	public int process(Function<String,Integer> f,int z){
		return f.apply("Foo") + z;
	}
	
	
	
	@Test
	public void testUsingFunction(){
			int process = process(new Function<String, Integer>() {

				@Override
				public Integer apply(String t) {
					return t.length();
				}
			}, 10);
			
			Assert.assertEquals(13, process);
	}
	
	@Test
	public void testUsingFunctionUsingLambda(){
			int process = process(t -> t.length(), 10);
			
			Assert.assertEquals(13, process);
	}
	
	@Test
	public void testStreamWithForEach(){
		Stream.generate(()->LocalDateTime.now())
			.limit(100)
			.distinct()
			.filter(t->t.getSecond() %2 ==0)
			.forEach(t->System.out.println(t));;
		
		//another way of doing it  - equivalent to above
		//Stream.generate(LocalDateTime::now).forEach(System.out::println);;

		
	}
	@Test
	public void testStreamWithCollect(){
		List<Integer> localDateTimes = Stream.generate(()->LocalDateTime.now())
			.limit(100)
			.distinct()
			.filter(localDateTime->localDateTime.getSecond() %2 ==0)
			.map(localDateTime->localDateTime.getMinute())
			.collect(Collectors.toList());
		
		System.out.println(localDateTimes);

		
	}
	@Test
	public void testStreamWithFlatMap(){
		IntStream.range(1, 4)
	    .mapToObj(i -> new Foo("Foo" + i))
	    .peek(f -> IntStream.range(1, 4)
	        .mapToObj(i -> new Bar("Bar" + i + " --> " +f.name))
	        .forEach(f.bars::add))
	    .flatMap(f -> f.bars.stream())
	    .forEach(b -> System.out.println(b.name));
		
	}
	
	@Test
	public void testStreamWithReduce(){
		OptionalInt reduce = IntStream.range(1, 4)
	    .reduce((x1,x2)->(x1>x2?x1:x2));
		assertTrue(reduce.getAsInt() == 3);
	    
	}
	@Test
	public void testStreamWithReduce1(){
		Collection<Person> persons = Lists.newArrayList(new Person("vin",20) , new Person("test", 1) , new Person("first",10));
		Person identity = new Person("", 0);
		Person result =
			    persons
			        .stream()
			        .reduce(identity, (p1, p2) -> {
			        	System.out.println(p1);
			            p1.age += p2.age;
			            p1.name += p2.name;
			            return p1;
			        });
		System.err.println(identity);
	    
	}
		
		
	
	class Foo {
	    String name;
	    List<Bar> bars = new ArrayList<>();

	    Foo(String name) {
	        this.name = name;
	    }
	}

	class Bar {
	    String name;

	    Bar(String name) {
	        this.name = name;
	    }
	}
	

	@Test
	public void testStreamWithMap(){
		List<Double> collect = Stream.of(1,2,3,4,5,6).map(t->Double.valueOf(t)+3.14).
				collect(Collectors.toList());
		System.out.println(collect);
	}
	
	@Test
	public void testStreamSortedWithMap(){
		Stream.of("d2", "a2", "b1", "b3", "c")
	    .sorted((s1, s2) -> {
	        System.out.printf("sort: %s; %s\n", s1, s2);
	        return s1.compareTo(s2);
	    })
	    .filter(s -> {
	        System.out.println("filter: " + s);
	        return s.startsWith("a");
	    })
	    .map(s -> {
	        System.out.println("map: " + s);
	        return s.toUpperCase();
	    })
	    .forEach(s -> System.out.println("forEach: " + s));
	}
	@Test
	public void testStreamWithAnyMatch(){
		boolean collect = Stream.of(1,2,3,4,5,6)
				.map(t->Double.valueOf(t)+3.14)
				.anyMatch(t->(8.14==t));
		System.out.println(collect);
	}
	@Test
	public void testStreamEquivalentInRXJava(){
		Stream.generate(()->LocalDateTime.now())
			.limit(100)
			.distinct()
			.filter(t->t.getSecond() %2 ==0)
			.forEach(t->System.out.println(t));;
		
		//another way of doing it  - equivalent to above
		//Stream.generate(LocalDateTime::now).forEach(System.out::println);;

		
	}
	
}
