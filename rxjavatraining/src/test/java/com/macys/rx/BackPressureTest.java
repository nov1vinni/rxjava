package com.macys.rx;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import org.junit.Test;

public class BackPressureTest {
	Observable<Object> crazyObservable = Observable.create(new ObservableOnSubscribe<Object>() {

		@Override
		public void subscribe(ObservableEmitter<Object> e) throws Exception {
			int count=0;
			while(true){
				e.onNext(count++);
			}
			
		}
	});
	
	
	Flowable<Object> crazyFlowable = Flowable.create(e -> {
		int count=0;
		while(true){
			e.onNext(count++);
		}
		
	},BackpressureStrategy.ERROR);
	
	Flowable<Object> crazyFlowableWithBuffer = Flowable.create(e -> {
		int count=0;
		while(true){
			e.onNext(count++);
		}
		
	},BackpressureStrategy.BUFFER);
	
	Flowable<Object> crazyFlowableWithBufferWitLargeData = Flowable.create(e -> {
		while(true){
			char[] s = new char[1024 * 10];
			String str = String.copyValueOf(s);
			e.onNext(str);
		}
		
	},BackpressureStrategy.BUFFER);
	Flowable<Object> crazyFlowableWithDrop = Flowable.create(e -> {
		int count=0;
		while(true){
			e.onNext(count++);
		}
		
	},BackpressureStrategy.DROP);
	
	Flowable<Object> crazyFlowableWithLatest = Flowable.create(e -> {
		int count=0;
		while(true){
			e.onNext(count++);
		}
		
	},BackpressureStrategy.DROP);



	
	@Test
	public void testSlowSubscriberFromObsrvableWithError() throws Exception{
	
		
		crazyObservable.observeOn(Schedulers.computation()).subscribe(data->{
			Thread.sleep(5);
			System.out.println(data);
		});
	}
	
	@Test
	public void testSlowSubscriberFromFlowableWithError() throws Exception{
	
		
		crazyFlowable.observeOn(Schedulers.computation()).subscribe(data->{
			Thread.sleep(5);
			System.out.println(data);
		});
	}
	@Test
	public void testSlowSubscriberFromFlowableWithBuffer() throws Exception{
	
		
		crazyFlowableWithBuffer.observeOn(Schedulers.computation()).subscribe(data->{
			Thread.sleep(5);
			System.out.println(data);
		});
	}
	
	@Test
	public void testSlowSubscriberFromFlowableWithBufferAndLargeDataChunks() throws Exception{
	
		
		crazyFlowableWithBufferWitLargeData.observeOn(Schedulers.computation()).subscribe(data->{
			Thread.sleep(5);
			System.out.println(data);
		});
	}
	@Test
	public void testSlowSubscriberFromFlowableWithBufferWithExtendedBuffer() throws Exception{
	
		
		crazyFlowableWithBuffer.observeOn(Schedulers.computation())
		.onBackpressureBuffer(4000)
		.subscribe(data->{
			Thread.sleep(5);
			System.out.println(data);
		});
	}
	
	@Test
	public void testSlowSubscriberFromFlowableWithDrop() throws Exception{
	
		
		crazyFlowableWithDrop.observeOn(Schedulers.computation()).subscribe(data->{
			Thread.sleep(5);
			System.out.println(data);
		});
	}
	@Test
	public void testSlowSubscriberFromFlowableWithLatest() throws Exception{
	
		
		crazyFlowableWithLatest.observeOn(Schedulers.computation()).subscribe(data->{
			Thread.sleep(5);
			System.out.println(data);
		});
	}
	
	
}
