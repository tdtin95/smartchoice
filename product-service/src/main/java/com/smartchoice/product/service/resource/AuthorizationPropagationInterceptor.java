package com.smartchoice.product.service.resource;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory.AUTHORIZATION_HEADER;


public class AuthorizationPropagationInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set(AUTHORIZATION_HEADER, "Bearer " + new TokenConsumer().getToken());
        return execution.execute(request, body);
    }
}
