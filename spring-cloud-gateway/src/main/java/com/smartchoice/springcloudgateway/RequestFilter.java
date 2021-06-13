package com.smartchoice.springcloudgateway;

import brave.Tracer;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-1)
@Component
public class RequestFilter implements GlobalFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

    private Tracer tracer;


    @Autowired
    public RequestFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOGGER.info("Routing to {}", exchange.getRequest().getPath().value());
        FilterUtils.setRequestHeader(exchange, "username", getUsername(exchange.getRequest().getHeaders()));
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() ->
                        FilterUtils.setResponseHeader(exchange, FilterUtils.CORRELATION_ID,
                                tracer.currentSpan().context().traceIdString())));
    }

    private String getUsername(HttpHeaders requestHeaders) {
        String username = "";
        if (FilterUtils.getAuthToken(requestHeaders) != null) {
            String authToken = FilterUtils.getAuthToken(requestHeaders).replace("Bearer ", "");
            JSONObject jsonObj = decodeJWT(authToken);
            try {
                username = jsonObj.getString("preferred_username");
            } catch (Exception e) {
                LOGGER.debug(e.getMessage());
            }
        }
        return username;
    }


    private JSONObject decodeJWT(String JWTToken) {
        String[] split_string = JWTToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        JSONObject jsonObj = new JSONObject(body);
        return jsonObj;
    }

}
