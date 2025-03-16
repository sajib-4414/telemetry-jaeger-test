package com.sajib.opentelemetry.opennn;

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ObConfig {
    private static final Logger logger = LoggerFactory.getLogger(ObConfig.class);
    @Bean
    OtlpHttpSpanExporter otlpHttpSpanExporter(@Value("${tracing.url}") String url) {
        return OtlpHttpSpanExporter.builder()
                .setEndpoint(url)
                .build();
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors((request, body, execution) -> {
                    logger.info("Outgoing Headers: {}", request.getHeaders());
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    public TextMapPropagator textMapPropagator() {
        return TextMapPropagator.composite(
                W3CTraceContextPropagator.getInstance(),
                W3CBaggagePropagator.getInstance()
        );
    }
}
