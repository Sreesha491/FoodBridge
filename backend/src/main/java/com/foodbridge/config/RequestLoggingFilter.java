package com.foodbridge.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that logs every incoming HTTP request and its response.
 *
 * <p>For each request this filter:
 * <ol>
 *   <li>Generates a unique {@code requestId} (UUID) and stores it in MDC so it appears
 *       in every log line emitted during request processing.</li>
 *   <li>Logs the incoming method + URI.</li>
 *   <li>Measures elapsed time and logs the response status on completion.</li>
 *   <li>Clears MDC to prevent leakage to the thread pool.</li>
 * </ol>
 *
 * <p>Runs first in the filter chain ({@code @Order(1)}).
 */
@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_KEY = "requestId";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        MDC.put(REQUEST_ID_KEY, requestId);

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String fullUri = query != null ? uri + "?" + query : uri;

        log.debug("→ {} {}", method, fullUri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            // Log at WARN level for 4xx/5xx, DEBUG for 2xx/3xx
            if (status >= 500) {
                log.warn("← {} {} {} ({}ms)", status, method, fullUri, elapsed);
            } else if (status >= 400) {
                log.info("← {} {} {} ({}ms)", status, method, fullUri, elapsed);
            } else {
                log.debug("← {} {} {} ({}ms)", status, method, fullUri, elapsed);
            }

            MDC.remove(REQUEST_ID_KEY);
        }
    }

    /** Skip logging for Swagger UI and actuator endpoints to reduce noise. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.contains("/swagger-ui")
                || uri.contains("/api-docs")
                || uri.contains("/actuator");
    }
}
