package com.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ReactiveFlowApp {

    private static final long MAX_SECONDS_TO_KEEP_IT_WHEN_NO_SPACE = 2;
    private static final Logger log = LoggerFactory.getLogger(ReactiveFlowApp.class);

    public void magazineDeliveryExample(final long sleepTimeJack,
                                 final long sleepTimePete,
                                 final int maxStorageInPO) throws Exception {
        final SubmissionPublisher<Integer> publisher =
                new SubmissionPublisher<>(ForkJoinPool.commonPool(), maxStorageInPO);

        final MagazineSubscriber jack = new MagazineSubscriber(
                sleepTimeJack,
                MagazineSubscriber.JACK
        );
        final MagazineSubscriber pete = new MagazineSubscriber(
                sleepTimePete,
                MagazineSubscriber.PETE
        );

        publisher.subscribe(jack);
        publisher.subscribe(pete);

        log.info("Printing 20 magazines per subscriber, with room in publisher for "
                + maxStorageInPO + ". They have " + MAX_SECONDS_TO_KEEP_IT_WHEN_NO_SPACE +
                " seconds to consume each magazine.");
        IntStream.rangeClosed(1, 20).forEach((number) -> {
            log.info("Offering magazine " + number + " to consumers");
            final int lag = publisher.offer(
                    number,
                    MAX_SECONDS_TO_KEEP_IT_WHEN_NO_SPACE,
                    TimeUnit.SECONDS,
                    (subscriber, msg) -> {
                        subscriber.onError(
                                new RuntimeException("Hey " + ((MagazineSubscriber) subscriber)
                                        .getSubscriberName() + "! You are too slow getting magazines" +
                                        " and we don't have more space for them! " +
                                        "I'll drop your magazine: " + msg));
                        return false; // don't retry, we don't believe in second opportunities
                    });
            if (lag < 0) {
                log("Dropping " + -lag + " magazines");
            } else {
                log("The slowest consumer has " + lag +
                        " magazines in total to be picked up");
            }
        });

        // Blocks until all subscribers are done (this part could be improved
        // with latches, but this way we keep it simple)
        while (publisher.estimateMaximumLag() > 0) {
            Thread.sleep(500L);
        }

        // Closes the publisher, calling the onComplete() method on every subscriber
        publisher.close();
        // give some time to the slowest consumer to wake up and notice
        // that it's completed
        Thread.sleep(Math.max(sleepTimeJack, sleepTimePete));
    }

    private static void log(final String message) {
        log.info("===========> " + message);
    }

}
