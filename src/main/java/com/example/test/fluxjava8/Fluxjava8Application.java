package com.example.test.fluxjava8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.util.function.Tuple2;

import java.time.Duration;

import reactor.core.publisher.Flux;

@SpringBootApplication
@RestController
public class Fluxjava8Application {

    public static void main(String[] args) {
		SpringApplication.run(Fluxjava8Application.class, args);
	}

//	@GetMapping(value = "/", produces = "text/event-stream")
	@GetMapping(value = "/", produces = "application/stream+json")
    public Flux<Num> rootGet() {
        Flux<java.lang.Integer> sampleFlux = Flux.just(1, 9, 4, 7, 6, 2, 2, 7, 3, 4, 8);

        final Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        return interval.zipWith(sampleFlux).map(Tuple2::getT2).map(Num::of);
    }

    public static class Num {
        private String text = "Babbel";
        private Integer val;

        public static Num of(Integer n) {
            final Num num = new Num();
            num.setVal(n);
            return num;
        }

        public void setVal(Integer val) {
            this.val = val;
        }

        public Integer getVal() {
            return val;
        }

        public String getText() {
            return text;
        }
    }
}
