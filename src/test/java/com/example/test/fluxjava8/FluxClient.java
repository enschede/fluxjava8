package com.example.test.fluxjava8;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class FluxClient {

    private static final Logger logger = LoggerFactory.getLogger(FluxClient.class);

    @Test
    public void testClient() throws InterruptedException {

        final WebClient producerClient = WebClient.create("http://localhost:8080/");

        Flux<Fluxjava8Application.Num> entries = producerClient.get()
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Fluxjava8Application.Num.class));

        entries.subscribe(n -> logger.info("Value: {}", n.getVal()));

        Thread.sleep(20500L);
    }
}
