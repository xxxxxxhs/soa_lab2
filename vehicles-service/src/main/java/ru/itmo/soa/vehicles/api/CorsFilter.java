package ru.itmo.soa.vehicles.api;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) {
        if (HttpMethod.OPTIONS.equals(request.getMethod())
                && request.getHeaderString("Origin") != null
                && request.getHeaderString("Access-Control-Request-Method") != null) {
            request.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (request.getHeaderString("Origin") == null) {
            return;
        }
        MultivaluedMap<String, Object> headers = response.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", "*");
        headers.putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        headers.putSingle("Access-Control-Allow-Headers", "Content-Type, Accept");
        headers.putSingle("Access-Control-Expose-Headers", "Location");
        headers.putSingle("Access-Control-Max-Age", "86400");
    }
}
