package com.example.test.fluxjava8;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.function.BiFunction;

public class FluxTest8 {

    private Flux<Integer> linearRangeFlux = Flux.range(10, 11);
    private Flux<Integer> sampleFlux = Flux.just(1, 9, 4, 7, 6, 2, 2, 7, 3, 4, 8);

    @Test
    public void flux01() {
        linearRangeFlux
                .doOnSubscribe(s -> System.out.println("New subscription"))
                .subscribe(System.out::println, System.out::println, () -> System.out.println("Complete!"));
    }

    @Test
    public void fluxReduce() {
        linearRangeFlux
                .scan(Pair.of(0,0), (pair1, newValue) -> pair1.shift(newValue))
                .filter(pair -> pair.first!=0)
                .subscribe(p -> System.out.println(p.gemiddelde()));
    }

    @Test
    public void flux02() {
        sampleFlux.distinctUntilChanged().subscribe(System.out::println);
    }

    @Test
    public void flux03() {
        sampleFlux.count().subscribe(System.out::println);
    }

    @Test
    public void flux03a() {
        sampleFlux.buffer(3).subscribe(System.out::println);
    }

    @Test
    public void flux04() throws InterruptedException {
        final Flux<Long> interval = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1));

        interval.subscribe(n -> System.out.println(n));

        Thread.sleep(10500L);
    }

    @Test
    public void flux05() throws InterruptedException {
        final Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        interval.zipWith(sampleFlux).map(Tuple2::getT2).subscribe(System.out::println);

        Thread.sleep(10500L);
    }


    private static class Pair {
        private int first;
        private int last;

        public static Pair of(int first, int last) {
            final Pair pair = new Pair();

            pair.setFirst(first);
            pair.setLast(last);

            return pair;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public int getFirst() {
            return first;
        }

        public void setLast(int last) {
            this.last = last;
        }

        public int getLast() {
            return last;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "first=" + first +
                    ", last=" + last +
                    '}';
        }

        Pair shift(Integer newValue) {
            return Pair.of(this.last, newValue);
        }

        public float gemiddelde() {
            return ((float)(first+last))/2.0f;
        }
    }
}
