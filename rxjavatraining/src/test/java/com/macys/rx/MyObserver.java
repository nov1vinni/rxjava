package com.macys.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MyObserver implements Observer<Object>{

	@Override
	public void onSubscribe(Disposable d) {
System.out.println("onSubscribe");		
	}

	@Override
	public void onNext(Object t) {
		System.out.println("onNext : " + t);		
		
	}

	@Override
	public void onError(Throwable e) {
		System.out.println("onError : " + e);		
		
	}

	@Override
	public void onComplete() {
		System.out.println("onComplete : ");		
		
	}
	

}
