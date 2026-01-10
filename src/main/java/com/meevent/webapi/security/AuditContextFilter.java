package com.meevent.webapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditContextFilter extends OncePerRequestFilter {

    private final JdbcTemplate jdbcTemplate;

    public AuditContextFilter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Fixed origin
            jdbcTemplate.execute("SET app.origin = 'API'");

            // Client IP
            String ipAddress = request.getRemoteAddr();
            jdbcTemplate.execute("SET app.ip_address = '" + ipAddress + "'");

            // User-Agent
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null) {
                jdbcTemplate.execute(
                        "SET app.user_agent = '" + userAgent.replace("'", "''") + "'"
                );
            }

            // Authenticated user context
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null
                    && authentication.getPrincipal() instanceof UserDetailsImpl details) {

                jdbcTemplate.execute(
                        "SET app.user_id = " + details.getId()
                );
                jdbcTemplate.execute(
                        "SET app.user_email = '" + details.getUsername() + "'"
                );
            }

            filterChain.doFilter(request, response);

        } finally {
            // Cleanup context variables
            jdbcTemplate.execute("RESET app.origin");
            jdbcTemplate.execute("RESET app.user_id");
            jdbcTemplate.execute("RESET app.user_email");
            jdbcTemplate.execute("RESET app.user_name");
            jdbcTemplate.execute("RESET app.ip_address");
            jdbcTemplate.execute("RESET app.user_agent");
        }
    }
}
