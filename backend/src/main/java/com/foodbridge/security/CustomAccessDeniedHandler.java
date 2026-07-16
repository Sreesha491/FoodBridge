package com.foodbridge.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodbridge.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom access-denied handler that returns a JSON {@link ApiResponse}
 * on 403 Forbidden, instead of Spring Security's default HTML error page.
 *
 * <p>Triggered when an authenticated user tries to access an endpoint
 * their role does not permit (e.g., a DONOR calling an ADMIN-only endpoint).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access denied for [{}] on [{}]: {}",
                request.getRemoteUser(), request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body = ApiResponse.error(
                "You do not have permission to access this resource.",
                "Forbidden"
        );
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
