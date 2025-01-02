package com.csye6225_ChenniXu.healthcheck.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NoPayloadFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Add the Cache-Control header to prevent caching
        httpResponse.setHeader("Cache-Control", "no-cache");

        // Only check GET requests on the /healthz endpoint
        if ("GET".equalsIgnoreCase(httpRequest.getMethod()) && "/healthz".equals(httpRequest.getRequestURI())) {
            // Check if the content length is greater than 0 which indicates a payload exists
            if (httpRequest.getContentLength() > 0) {
                // Return 400 Bad Request if payload exists
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Check if there are any query parameters
            if (httpRequest.getQueryString() != null && !httpRequest.getQueryString().isEmpty()) {
                // Return 400 Bad Request if any query parameters exist
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        // Proceed with the normal flow if no payload
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}