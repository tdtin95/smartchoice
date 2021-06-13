package com.smartchoice.springcloudgateway;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class LoggingGatewayFactory extends AbstractGatewayFilterFactory<LoggingGatewayFactory.Config> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingGatewayFactory.class);

    public LoggingGatewayFactory() {
        super(LoggingGatewayFactory.Config.class);
    }



    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            LOGGER.info(config.getPreMessage());
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        LOGGER.info(config.getPostMessage());
                    }));
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("preMessage", "postMessage");
    }

    @Data
    @NoArgsConstructor
    public static class Config {
        private String postMessage;
        private String preMessage;
    }
}
