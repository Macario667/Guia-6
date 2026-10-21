package com.inklusport;

import java.util.UUID;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logmanager.MDC;

@Provider
public class RequestIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String REQUEST_ID_PROPERTY = RequestIdFilter.class.getName() + ".requestId";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String requestId = UUID.randomUUID().toString();
        requestContext.setProperty(REQUEST_ID_PROPERTY, requestId);
        MDC.put("request.id", requestId);
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {
        try {
            Object requestId = requestContext.getProperty(REQUEST_ID_PROPERTY);
            if (requestId instanceof String id) {
                responseContext.getHeaders().putSingle("X-Request-Id", id);
            }
        } finally {
            MDC.remove("request.id");
        }
    }
}
