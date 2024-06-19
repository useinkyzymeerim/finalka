package com.finalka.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class UrlDecodeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpServletRequest) {
            @Override
            public String getParameter(String name) {
                String value = super.getParameter(name);
                try {
                    return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Failed to decode parameter", e);
                }
            }
        };
        chain.doFilter(requestWrapper, response);
    }
}