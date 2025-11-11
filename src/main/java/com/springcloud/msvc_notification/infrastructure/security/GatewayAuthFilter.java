package com.springcloud.msvc_notification.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class GatewayAuthFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String userRolesHeader = request.getHeader(USER_ROLES_HEADER);

        if (userIdHeader == null || userRolesHeader == null || userIdHeader.isBlank() || userRolesHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            AuthUser authUser = new AuthUser(userIdHeader, userRolesHeader);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    authUser,
                    null,
                    authUser.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("Error creating AuthUser from Gateway headers: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
