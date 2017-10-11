package com.macys.rx;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.util.Lists;
import org.junit.Test;

public class BasicObservableTests {
	
	
	/**
	 * oBSERVABLE -- > (SINK) OBSERVER
	 */
	@Test
	public void testBasicObservable(){
		Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter)
					throws Exception {
				emitter.onNext(40);
				emitter.onNext(30);
				//emitter.onError(new RuntimeException("Just bcoz i can "));
				emitter.onNext(20);
				emitter.onComplete();
			}
		});
		
		System.out.println("Done creating observable");
		//attach and observer
		observable.subscribe(new Observer<Object>() {

			@Override
			public void onComplete() {
				System.out.println("onComplete ");
				
			}

			@Override
			public void onError(Throwable arg0) {
				System.out.println("onError " + arg0);
				throw new RuntimeException("exception from onError");
			}

			@Override
			public void onNext(Object arg0) {
				System.out.println("onNext " + arg0);
			//	if((Integer)arg0 == 30 ) throw new RuntimeException("exception from onnext");
				
			}

			@Override
			public void onSubscribe(Disposable arg0) {
				System.out.println("onSubscribe " + arg0);
			}
		});
	
		System.err.println("SECOND WAY ");
	 Observable<Integer> map = observable
		.map(t->2+t);
	 
	map.subscribeWith(new MyObserver());
	
		
	}
	
	
	@Test
	public void testBasicObservableWitLambda(){
	
		 Observable.<Integer>create(emitter->{
			emitter.onNext(40);
			emitter.onNext(30);
			//emitter.onError(new RuntimeException("Just bcoz i can "));
			emitter.onNext(20);
			emitter.onComplete();
		}).map(integer->integer+2)
		.subscribe(new Observer<Object>() {

			@Override
			public void onComplete() {
				System.out.println("onComplete ");
				
			}

			@Override
			public void onError(Throwable arg0) {
				System.out.println("onError " + arg0);
				throw new RuntimeException("exception from onError");
			}

			@Override
			public void onNext(Object arg0) {
				System.out.println("onNext " + arg0);
			//	if((Integer)arg0 == 30 ) throw new RuntimeException("exception from onnext");
				
			}

			@Override
			public void onSubscribe(Disposable arg0) {
				System.out.println("onSubscribe " + arg0);
			}
		});
	
		
	
		
	}
	
	@Test
	public void testBasicObservableWitLambdaFork(){
	
		 Observable<Integer> afterMap = Observable.<Integer>create(emitter->{
			emitter.onNext(40);
			emitter.onNext(30);
			//emitter.onError(new RuntimeException("Just bcoz i can "));
			emitter.onNext(20);
			emitter.onComplete();
		}).map(integer->integer+2);
		 
		MyObserver observer = new MyObserver();
		
		afterMap.subscribe(observer);
	
		
		afterMap.map(t->"Hello:" + t)
				.subscribe(observer);
		
	}
	

	@Test
	public void testBasicObservableWitLambdaForkAndActions() {

		Observable<Integer> observable = Observable.create(emitter -> {
			emitter.onNext(40);
			emitter.onNext(30);
			// emitter.onError(new RuntimeException("Just bcoz i can "));
				emitter.onNext(20);
				emitter.onComplete();
			});

			observable.subscribe(t->System.out.println(t), 
								 t -> t.printStackTrace(),
								 new Action() {
									 
									@Override
									public void run() throws Exception {
										System.out.println("Done !!");
									}
								});

	}
	
	@Test
	public void testBasicObservableWitJustForkAndActions() {

		Observable<Integer> observable = Observable.just(10,20,30);

			observable.subscribe(t->{System.out.println(t);System.out.println("On next thread :"+Thread.currentThread().getName());}, 
								 t -> {t.printStackTrace();System.out.println("On error : "+Thread.currentThread().getName());},
								 new Action() {
									 
									@Override
									public void run() throws Exception {
										System.out.println("Done !!");
										System.out.println("On run "+Thread.currentThread().getName());
									}
								});

	}

	
	
	@Test
	public void testBasicObservableWithNonDefer() throws InterruptedException{
		
		Observable<LocalDateTime> deferObservable = Observable.just(LocalDateTime.now());
		
		
		deferObservable.subscribeWith(new MyObserver()); //this1
		Thread.sleep(2000);
		deferObservable.subscribeWith(new MyObserver());//this2 will have same tresult as this1
	}
	
	@Test
	public void testBasicObservableWithDefer() throws InterruptedException{
		
		Observable<LocalDateTime> deferObservable = Observable.defer(new Callable<ObservableSource<LocalDateTime>>() {

			@Override
			public ObservableSource<LocalDateTime> call() throws Exception {
				return Observable.just(LocalDateTime.now());
			}
		});
		
		
		deferObservable.subscribeWith(new MyObserver());
		Thread.sleep(2000);
		deferObservable.subscribeWith(new MyObserver());
	}
	
	@Test
	public void testBasicObservableWithDeferAndRepeat() throws InterruptedException{
		
		Observable<LocalDateTime> deferObservable = Observable.defer(new Callable<ObservableSource<LocalDateTime>>() {

			@Override
			public ObservableSource<LocalDateTime> call() throws Exception {
				System.out.println("We are invoked again");
				return Observable.just(LocalDateTime.now());
			}
		});
		
		
		deferObservable.subscribeWith(new MyObserver());
		Thread.sleep(2000);
		deferObservable.repeat(10).subscribeWith(new MyObserver());
	}
	
	@Test
	public void testBasiObservableWithInterval() throws InterruptedException{
		
		Observable.interval(1, TimeUnit.SECONDS)
				.doOnNext(n->System.out.println(Thread.currentThread().getId() + Thread.currentThread().getName()))
				.subscribe(t->{System.out.println("on next: " + t);});
		Thread.sleep(10000);
	}
	
	@Test
	public void testBasiObservableWithRepeatAndNoComplete() throws InterruptedException{
		//this retursn only 2,3,4
		Observable.<Integer>create(t->{t.onNext(1);t.onNext(2);t.onNext(3);})
				  .map(t->t+1)
				  .repeat(3)
				  .subscribe(t->System.out.println(t),t->System.out.println("Done!!"));
		
		//Repeat will repeat only when the first operation is complete. 
	}
	
	
	@Test
	public void testBasiObservableWithRepeat() throws InterruptedException{
		//this returns 2,3,4,2,3,4,2,3,4
		Observable.<Integer>create(t->{t.onNext(1);t.onNext(2);t.onNext(3);t.onComplete();})
				  .map(t->t+1)
				  .repeat(3)
				  .subscribe(t->System.out.println(t),t->System.out.println("Done!!"));
		
		//Repeat will repeat only when the first operation is complete. 
	}
	
	@Test
	public void testVoilationOfContactDoNoTDoThisSwearDONOTDOTHIS() throws Exception{
		//never interlink threads when you are emitting from observables
		Observable.<Integer>create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> e)
					throws Exception {
					Thread t1 = new Thread(){
						@Override
						public void run() {
							e.onNext(1);
							e.onNext(2);

						}
					};
					t1.start();
					
					Thread t2 = new Thread(){
						@Override
						public void run() {
							e.onNext(1);
							e.onNext(2);

						}
					};
					t2.start();
				
			}
		});
		
	}
	
	
	
	@Test
	public void testClosureWithFilter(){
		int adult = 18;
		Observable.<Integer>just(18,4,10,22,33,50,66).filter(x->x>=adult).subscribe(t->System.out.println(t));
		
	}
	
	@Test
	public void testFlatMap(){
		Observable.<Integer>just(1,2,3,4).map(t->Lists.newArrayList(-t,t,t+1)).forEach(t->System.out.println(t));
		
	}
	@Test
	public void testFlatMapWithFlatten(){
		Observable<Integer> flatMap = Observable.<Integer>just(1,2,3,4).flatMap(i -> Observable.just(-i,i,i+1));
		flatMap.subscribe(t->System.out.println(t));
	}
	
	@Test
	public void testJAVA8StreamWithFlatMap(){
		Stream.<Integer>of(1,2,3,4).
				flatMap(i -> Stream.of(-i,i,i+1)).
				collect(Collectors.toList()).
				forEach(t->System.out.println(t));
	}
	@Test
	public void testZip(){
		Observable<Integer> range  = Observable.range(1, 10);
		Observable<Character> characterObservale  = Observable.range(97, 26).map(i->(char)i.intValue());
		
		
		Observable<String> stringZipOBservable = range.zipWith(characterObservale, new BiFunction<Integer, Character, String>() {

			@Override
			public String apply(Integer t1, Character t2) throws Exception {
				return "("+t1+","+t2+")";
			}
		});
		
		stringZipOBservable.subscribe(t->System.out.println(t));
		
	}
	@Test
	public void testZipWithEmptySecond(){
		Observable<Integer> range  = Observable.range(1, 10);
		Observable<Character> characterObservale  = Observable.empty();
		
		
		Observable<String> stringZipOBservable = range.zipWith(characterObservale, new BiFunction<Integer, Character, String>() {

			@Override
			public String apply(Integer t1, Character t2) throws Exception {
				return "("+t1+","+t2+")";
			}
		});
		
		stringZipOBservable.subscribe(t->System.out.println(t),Throwable::printStackTrace,()->System.out.println("doNE"));
		
	}
	@Test
	public void testReduce(){
		Maybe<Integer> reduce = Observable.range(1, 10).reduce((total,t2)->{
				System.out.println("Inte1 :"+total + "  Inte2 : "+t2);
				return total+t2;
			}
		);
		
		
		reduce.subscribe(total->System.out.println(total),Throwable::printStackTrace,()->System.out.println("dONE"));
		
		reduce.subscribe(new MaybeObserver() {

			@Override
			public void onSubscribe(Disposable d) {
				System.out.println("subscribing");
				
			}

			@Override
			public void onSuccess(Object t) {
				System.out.println(t);
			}

			@Override
			public void onError(Throwable e) {
				System.out.println(e);
				
			}

			@Override
			public void onComplete() {
				System.out.println("Done");
				
			}
		});
	}
	@Test
	public void testOptional(){
		Optional<String> test = Optional.of("String");
		
		System.out.println(test.orElse("default"));
	}
	
	@Test
	public void testFnFactorial(){
		int end=5;
		Single<Integer> reduce = Observable.range(1, end).reduce(1, (total,t2)->total*t2);
		reduce.subscribe(t1->System.out.println(t1));
	}
	
	@Test
	public void testFnFactorialAsReturnForReduce(){
		int end=5;
		Single<ArrayList<Object>> reduce = Observable.range(1, end).reduce(Lists.newArrayList(1,2), (total,t2)->{
				System.err.println("total : "+total);
				total.add(t2);
				return total;
			});
		reduce.subscribe(t1->System.out.println(t1));
	}
	@Test
	public void testCollect(){
		Observable.range(1, 20).collect(new Callable<List<String>>() {

			@Override
			public List<String> call() throws Exception {
				List<String> test = new ArrayList<>();
				test.add("defa");
				return test;
			}
		}, new BiConsumer<List<String>, Integer>() {

			@Override
			public void accept(List<String> t1, Integer t2) throws Exception {
				t1.add("Hello" + t2);
			}
		}).subscribe(t1->System.out.println(t1));
	}
	
	@Test
	public void testCollectwITHConcat(){
		
		Observable<String> concat = Observable.concat(Observable.just("foo","bar"),
				Observable.range(1, 20).map(t->"hello!"+t));

		Single<ArrayList<String>> collect = concat.collect(ArrayList::new, new BiConsumer<List<String>, String>() {

			@Override
			public void accept(List<String> t1, String t2) throws Exception {
				t1.add(t2);
				
			}
		});
		collect.subscribe(t1->System.out.println(t1));
		
		System.err.println(concat.toList());
	}
	
}
