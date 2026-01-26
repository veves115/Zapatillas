package es.pabloab.zapatillas.config.auth;


import es.pabloab.zapatillas.rest.auth.services.AuthenticationService;
import es.pabloab.zapatillas.rest.auth.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthenticationService authUserService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws IOException, ServletException{
        log.info("Iniciando el filtro de autenticacion");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        UserDetails userDetails = null;
        String userName = null;

        //
    }

}
