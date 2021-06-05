package com.smartchoice.api.gateway;

import brave.Tracer;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationPreFilter extends ZuulFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationPreFilter.class);
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Autowired
    public AuthenticationPreFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    private Tracer tracer;


    @Override
    public Object run() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
            String username = (String) token.getTokenAttributes().get("preferred_username");
            RequestContext requestContext = RequestContext.getCurrentContext();
            requestContext.addZuulRequestHeader("username", username);
            requestContext.getResponse().addHeader("tmx-correlation-id", tracer.currentSpan().context().traceIdString());
        }
        return null;
    }
}
