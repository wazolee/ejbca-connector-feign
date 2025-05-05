package com.learn.ejbcaconnectorfeign.service;

import com.learn.ejbcaconnectorfeign.dto.EnrollKeystoreRequest;
import com.learn.ejbcaconnectorfeign.dto.EnrollKeystoreResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EjbcaService {

    private final WebClient webClient;

    public EjbcaService(WebClient ejbcaWebClient) {
        this.webClient = ejbcaWebClient;
    }

    public Mono<EnrollKeystoreResponse> enrollKeystore(EnrollKeystoreRequest request) {
        return webClient.post()
                .uri("/v1/certificate/enrollkeystore")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EnrollKeystoreResponse.class)
                .doOnError(throwable -> {
                    throw new RuntimeException("Failed to enroll keystore: " + throwable.getMessage(), throwable);
                });
    }
}