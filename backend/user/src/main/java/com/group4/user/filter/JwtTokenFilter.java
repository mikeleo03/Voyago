package com.group4.user.filter;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.group4.user.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService; // Custom JWT service for decoding the token

    @Autowired
    public JwtTokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            
            // Decode JWT and store the username in the SecurityContextHolder
            String username = tokenService.extractUsername(jwt);
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
}

