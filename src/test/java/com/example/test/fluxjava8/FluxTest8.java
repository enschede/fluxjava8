package com.example.test.fluxjava8;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;

class FluxTest8 {

    private Flux<Integer> sampleFlux = Flux.just(1, 9, 4, 7, 6, 2, 2, 7, 3, 4, 8);

    // We love TDD
    // Given a flux, write a flux ending with a RuntimeException and write a verifier for it
    @Test
    void fluxAndVerifierTest() {
        Flux<Integer> sampleFlux = Flux
                .just(1, 9, 4, 7, 6, 2, 2, 7, 3, 4, 8)
                .concatWith(Mono.error(new RuntimeException("bla")));

        StepVerifier.create(sampleFlux)
                .expectNext(1, 9, 4, 7, 6, 2, 2, 7, 3, 4, 8)
                .expectError()
                .verify();
    }

    // We want to subscribe to the flux
    //
    // Given the flux, write 2 subscribers; one lambda based and one implemenation of the Subscriber interface
    @Test
    void fluxAndSubscriber() {

        sampleFlux.subscribe(System.out::println, System.out::println, () -> System.out.println("Complete"));

        sampleFlux.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("subscription: ");
                subscription.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNex: " + integer);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    // We want our flux to notify us on subscribe of a subscriber
    //
    // Given a flux, let the flux execute a println once it is subsribed
    @Test
    void flux01() {
        sampleFlux
                .doOnSubscribe(s -> System.out.println("New subscription"))
                .subscribe(x -> System.out.println(x),
                        x1 -> System.out.println(x1),
                        () -> System.out.println("Complete!"));
    }

    // Given a flux emitting elements, print all elements that are changed compared to previous element in the flux
    @Test
    void distinctFlux() {
        final Flux<Integer> actual = sampleFlux.distinctUntilChanged();

        actual.subscribe(System.out::println);

        StepVerifier.create(actual)
                .expectNext(1, 9, 4, 7, 6, 2, 7, 3, 4, 8)
                .expectComplete()
                .verify();
    }

    // Create a flux containing a _range_ [10, 11, 12, ... 20]
    // Verify the output
    @Test
    void rangeFlux() {
        Flux<Integer> linearRangeFlux = Flux.range(10, 11);

        StepVerifier.create(linearRangeFlux)
                .expectNext(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
                .verifyComplete();
    }

    // Create a flux containing an arbitraty list that emits one element per second
    // Send output to console
    @Test
    void intervalFlux() throws InterruptedException {
        final Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        interval.subscribe(System.out::println);

        Thread.sleep(10000L);
    }

    // Given one flux emitting a set of elements and one flux emitting one element per second,
    // combine them emitting the first flux one by one per second
    // Send output to console
    @Test
    void zipFlux() throws InterruptedException {
        final Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        interval.zipWith(sampleFlux).map(Tuple2::getT2).subscribe(System.out::println);

        Thread.sleep(10000L);
    }

    // Calculate the average of the last two element, skipping the first element
    // Send output to console
    @Test
    void showAverageOfLastTwoValues() {
        sampleFlux
                .scan(new Pair(0, 0), (pair1, newValue) -> pair1.shift(newValue))
                .skip(1)
                .subscribe(p -> System.out.println(p.average()));
    }

    private static class Pair {
        final int first;
        final int last;

        private Pair(int first, int last) {
            this.first = first;
            this.last = last;
        }

        Pair shift(Integer newValue) {
            return new Pair(this.last, newValue);
        }

        public float average() {
            return ((float) (first + last)) / 2.0f;
        }
    }
}
