package com.prj.LoneHPManagement.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
// For extracting claims from the JWT
import org.springframework.security.core.GrantedAuthority; // For representing authorities
import org.springframework.security.core.authority.SimpleGrantedAuthority; // For creating authority objects
import java.util.List; // For working with lists
import java.util.Collection; // For working with collections
import java.util.stream.Collectors;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader("Authorization");
        String userCode = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            userCode = jwtUtil.extractUsername(jwt); // Extract user identifier
        }

        if (userCode != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load full user details from your database including effective permissions
            UserDetails userDetails = userDetailsService.loadUserByUsername(userCode);
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
