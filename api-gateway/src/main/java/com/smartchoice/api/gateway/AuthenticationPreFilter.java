package com.smartchoice.api.gateway;

import com.netflix.zuul.ZuulFilter;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationPreFilter extends ZuulFilter {
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

    @Override
    public Object run() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication instanceof AnonymousAuthenticationToken)) {
//            String currentUserName = authentication.getName();
//            RequestContext requestContext = RequestContext.getCurrentContext();
//            requestContext.addZuulRequestHeader("Username", currentUserName);
//        }
        return null;
    }
}
