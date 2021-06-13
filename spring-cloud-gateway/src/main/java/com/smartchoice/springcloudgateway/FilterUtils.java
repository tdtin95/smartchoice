package com.smartchoice.springcloudgateway;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

public class FilterUtils {

    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN = "Authorization";


    public static String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            List<String> header = requestHeaders.get(CORRELATION_ID);
            return header.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public static String getAuthToken(HttpHeaders requestHeaders) {
        if (requestHeaders.get(AUTH_TOKEN) != null) {
            List<String> header = requestHeaders.get(AUTH_TOKEN);
            return header.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public static ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(
                exchange.getRequest().mutate()
                        .header(name, value)
                        .build())
                .build();
    }

    public static void setResponseHeader(ServerWebExchange exchange, String name, String value) {
        exchange.getResponse()
                .getHeaders().set(name, value);
    }

    public static ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

}
