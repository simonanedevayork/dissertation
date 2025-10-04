package com.york.doghealthtracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Filter that intercepts incoming HTTP requests
 */
@Component
@Log4j2
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String reqBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            String resBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

            String headers = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(h -> h, request::getHeader))
                    .toString();

            log.info("REQUEST {} {} headers={} body={}", request.getMethod(), request.getRequestURI(), headers, reqBody);
            log.info("RESPONSE {} status={} body={}", request.getRequestURI(), response.getStatus(), resBody);

            responseWrapper.copyBodyToResponse();
        }
    }

}