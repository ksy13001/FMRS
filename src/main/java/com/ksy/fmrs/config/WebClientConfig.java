package com.ksy.fmrs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig{
    @Value("${api-football.key}")
    private String apiFootballKey;

    @Value("${api-football.host}")
    private String apiFootballHost;

    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // 연결 타임아웃 5초
            .responseTimeout(Duration.ofSeconds(15))             // 전체 응답 타임아웃 15초
            .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(15))
                            .addHandlerLast(new WriteTimeoutHandler(15))
            );



    @Bean
    public WebClient webClient(ObjectMapper objectMapper) {
        // WebClient에 적용
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                })
                .build();

        return WebClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .codecs(configurer ->   // 최대 버퍼 사이즈
                        configurer.defaultCodecs().maxInMemorySize(2*1024 * 1024))
                .build();
    }
}
