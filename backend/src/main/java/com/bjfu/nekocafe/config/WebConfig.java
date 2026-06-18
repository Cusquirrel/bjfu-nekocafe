package com.bjfu.nekocafe.config;

import com.bjfu.nekocafe.common.TraceId;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Configuration
public class WebConfig implements WebMvcConfigurer, Filter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOriginPatterns("*").allowedMethods("GET","POST","PUT","DELETE","OPTIONS").allowedHeaders("*");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        TraceId.set(traceId);
        MDC.put("traceId", traceId);
        if (response instanceof HttpServletResponse) {
            ((HttpServletResponse) response).setHeader("X-Trace-Id", traceId);
        }
        try { chain.doFilter(request, response); } finally { TraceId.clear(); MDC.clear(); }
    }
}
