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
public class AuditoriaContextFilter extends OncePerRequestFilter {

    private final JdbcTemplate jdbcTemplate;

    public AuditoriaContextFilter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doFilterInternal(
             HttpServletRequest request,
             HttpServletResponse response,
             FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Origen fijo
            jdbcTemplate.execute("SET app.origen = 'API'");

            // IP real
            String ip = request.getRemoteAddr();
            jdbcTemplate.execute("SET app.ip_address = '" + ip + "'");

            // User-Agent
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null) {
                jdbcTemplate.execute("SET app.user_agent = '" + userAgent.replace("'", "''") + "'");
            }

            // Usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof UsuarioDetails details) {
                Long usuarioId = details.getId();
            }

            filterChain.doFilter(request, response);

        } finally {
            jdbcTemplate.execute("RESET app.origen");
            jdbcTemplate.execute("RESET app.usuario_id");
            jdbcTemplate.execute("RESET app.usuario_email");
            jdbcTemplate.execute("RESET app.usuario_nombre");
            jdbcTemplate.execute("RESET app.ip_address");
            jdbcTemplate.execute("RESET app.user_agent");
        }
    }
}
