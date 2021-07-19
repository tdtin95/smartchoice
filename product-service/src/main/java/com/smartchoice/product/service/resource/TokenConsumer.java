package com.smartchoice.product.service.resource;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

public class TokenConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenConsumer.class);

    public TokenConsumer() {
        LOGGER.info(String.valueOf(this.hashCode()));
    }


    public String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null && !KeycloakAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return "";
        }
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        KeycloakSecurityContext context = token.getAccount().getKeycloakSecurityContext();
        return context.getTokenString();
    }
}
