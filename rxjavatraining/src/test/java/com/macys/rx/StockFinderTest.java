package com.macys.rx;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.junit.Test;

public class StockFinderTest {
	
	private ExecutorService execService = Executors.newFixedThreadPool(4);
	public Future<String> createFuture(String stockURL) throws IOException{
		
		return execService.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				// TODO Auto-generated method stub
				String data = getDataFromURL(stockURL);
				//System.err.println("data for sotck: " +stockURL + "\n"+data);
				//return "THREAD :"+Thread.currentThread().getId()+Thread.currentThread().getName()+"Stock : "+stockURL + "\nData for it :"+data;
				return data;
			}
		});
	}

	private String getDataFromURL(String stockURL) throws MalformedURLException,
			IOException {
		System.out.println("Current thread : "+Thread.currentThread().getName() );
		URL url = new URL(stockURL);
		InputStream openStream = url.openStream();
		InputStreamReader reader = new InputStreamReader(openStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
		 return bufferedReader.lines().collect(Collectors.joining("\n"));
	}
	
	
	@Test
	public void testStockPriceFinderWithLatestQuoteAndFaultTolerance(){
		Observable<String> stockPrices = Observable.just("M","MSFT","BTC","T","ORCL");
		Observable<String> urls = stockPrices.map(t->"https://finance.google.com/finance/historical?output=csv&q="+t);
		
		
		
		Observable<String> contentObservable = urls.flatMap(url -> Observable.fromFuture(createFuture(url)).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends String>>() {

			@Override
			public ObservableSource<? extends String> apply(
					Throwable t)
					throws Exception {
				System.out
						.println("Throwable message"+t.getMessage());
				return Observable.empty();
			}
		}));
		
	
													;
		
		
		Observable<String> lineObservable = contentObservable.flatMap(doc->Observable.fromArray(doc.split("\n")).skip(1).take(1));
		lineObservable.subscribe(data->System.out.println(data),Throwable::printStackTrace,()->System.out.println("Done"));

		Observable<Double> latestStockValues = lineObservable.map(stock->stock.split(",")[4]).map(Double::parseDouble);
		stockPrices.zipWith(latestStockValues, 
								(BiFunction<String, Double, Tuple2<String, Double>>) (t1, t2) -> new Tuple2<String, Double>(t1, t2))
				   .subscribe(data->System.out.println(data));
		
	}
	@Test
	public void testStockPriceFinderWithlatestquote(){
		Observable<String> stockPrices = Observable.just("M","MSFT","T","ORCL");
		Observable<String> urls = stockPrices.map(t->"https://finance.google.com/finance/historical?output=csv&q="+t);
		
		Observable<String> contentObservable = urls.flatMap(url -> Observable.fromFuture(createFuture(url)));
		
		//contentObservable.flatMap(doc->Observable.fromArray(doc.split("\n"))).subscribe(data->System.out.println(data));

		Observable<String> lineObservable = contentObservable.flatMap(doc->Observable.fromArray(doc.split("\n")).skip(1).take(1));
		lineObservable.subscribe(data->System.out.println(data),Throwable::printStackTrace,()->System.out.println("Done"));

		Observable<Double> latestStockValues = lineObservable.map(stock->stock.split(",")[4]).map(Double::parseDouble);
		stockPrices.zipWith(latestStockValues, 
								(BiFunction<String, Double, Tuple2<String, Double>>) (t1, t2) -> new Tuple2<String, Double>(t1, t2))
				   .subscribe(data->System.out.println(data));
		
	}
	
	
	
	
	@Test
	public void testStockPriceFinderWithlatestquoteAndScheduler() throws InterruptedException{
		Observable<String> stockPrices = Observable.just("M","MSFT","T","ORCL");
		Observable<String> urls = stockPrices.map(t->"https://finance.google.com/finance/historical?output=csv&q="+t);
		
		//Observable<String> contentObservable = urls.flatMap(url -> Observable.fromFuture(createFuture(url)));

		
		urls.observeOn(Schedulers.from(execService)).map(s->getDataFromURL(s))
				.flatMap(doc->Observable.fromArray(doc.split("\n")).skip(1).take(1))
				.subscribe(t->System.out.println(t));
		
		Thread.sleep(10000);
	}
	
	@Test
	public void testStockPriceFinderWithlatestquoteAndSchedulerWIthdoonnext() throws InterruptedException{
		Observable<String> stockPrices = Observable.just("M","MSFT","T","ORCL");
		Observable<String> urls = stockPrices.map(t->"https://finance.google.com/finance/historical?output=csv&q="+t);
		
		//Observable<String> contentObservable = urls.flatMap(url -> Observable.fromFuture(createFuture(url)));
		urls.doOnNext(t->System.out.println("doonnext " +Thread.currentThread().getName()))
			.subscribeOn(Schedulers.from(execService))
			.map(s->getDataFromURL(s))
			.observeOn(Schedulers.io())
			.doOnNext(t->System.out.println("doonnext observeon " +Thread.currentThread().getName()))
				.flatMap(doc->Observable.fromArray(doc.split("\n")).skip(1).take(1))
				.subscribe(t->System.out.println(t));
		
		Thread.sleep(10000);
	}
	
	@Test
	public void testStockPriceSchedulerWithFlowable() throws InterruptedException{
		Flowable<String> stockPrices = Flowable.just("M","MSFT","T","ORCL");
		Flowable<String> urls = stockPrices.map(t->"https://finance.google.com/finance/historical?output=csv&q="+t);
		
		ParallelFlowable<String> flatMap = urls.parallel(4).runOn(Schedulers.from(execService))
										.map(this::getDataFromURL)
										.flatMap(doc->Flowable.fromArray(doc.split("\n")).skip(1).take(1));
		
		flatMap.sequential().subscribe(System.out::println);
		
		Thread.sleep(10000);
	}
}
