package com.example.test.fluxjava8;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class FluxClient {

    @Test
    public void testClient() throws InterruptedException {

        final WebClient producerClient = WebClient.create("http://localhost:8080/");

        Flux<Fluxjava8Application.Num> entries = producerClient.get()
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Fluxjava8Application.Num.class));

        entries.subscribe(n -> System.out.println(n.getVal()));

        Thread.sleep(20500L);
    }
}
