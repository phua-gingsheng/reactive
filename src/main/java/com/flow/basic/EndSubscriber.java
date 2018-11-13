package com.flow.basic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;

public class EndSubscriber<T> implements Subscriber<T> {

    private AtomicInteger howMuchMessagesConsume;
    private Subscription subscription;
    public List<T> consumedElements = new LinkedList<>();

    public EndSubscriber(Integer howMuchMessagesConsume) {
        System.out.println("EndSubscriber : " + howMuchMessagesConsume);
        this.howMuchMessagesConsume = new AtomicInteger(howMuchMessagesConsume);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("onSubscribe : " + subscription.hashCode());
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        howMuchMessagesConsume.decrementAndGet();
        System.out.println("onNext : " + item);
        consumedElements.add(item);
        if(howMuchMessagesConsume.get() > 0) {
            subscription.request(1);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("onError : ");
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
