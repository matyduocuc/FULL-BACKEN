package com.library.loans.client;

import com.library.loans.config.MicroservicesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final MicroservicesConfig microservicesConfig;

    public Mono<Boolean> validateUser(Long userId, String token) {
        String url = microservicesConfig.getUserManagement().getUrl() + "/api/users/" + userId;

        return webClientBuilder.build()
                .get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(error -> {
                    log.error("Error validando usuario {}: {}", userId, error.getMessage());
                    return Mono.just(false);
                });
    }
}

