package com.group4.gateway.config;

import com.group4.gateway.exceptions.JwtDecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class GatewaySecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewaySecurityConfig.class);

    @Value("classpath:certs/public-key.pem")
    private Resource publicKeyResource;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws JwtDecodingException {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                    .pathMatchers("/api/v1/auth/**").permitAll()
                    .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> {
                        try {
                            jwt.jwtDecoder(jwtDecoder());
                        } catch (Exception e) {
                            throw new JwtDecodingException("Failed to decode JWT", e);
                        }
                    }))
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() throws JwtDecodingException {
        try {
            RSAPublicKey publicKey = loadPublicKey();
            return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new JwtDecodingException("Error decoding JWT", e);
        }
    }

    private RSAPublicKey loadPublicKey() throws JwtDecodingException {
        try {
            byte[] keyBytes = publicKeyResource.getInputStream().readAllBytes();
            String publicKeyContent = new String(keyBytes)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            log.info("Public key content: {}", publicKeyContent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new JwtDecodingException("Failed to load the public key", e);
        }
    }    
}
