package com.group4.authentication.config;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

import com.group4.authentication.exceptions.JwtDecodingException;

@Component
public class RsaKeyLoader {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyLoader.class);

    @Value("classpath:certs/public-key.pem")
    private Resource publicKeyResource;

    @Value("classpath:certs/private-key.pem")
    private Resource privateKeyResource;

    private final RsaKeyConfigProperties rsaKeyConfigProperties;

    public RsaKeyLoader(RsaKeyConfigProperties rsaKeyConfigProperties) {
        this.rsaKeyConfigProperties = rsaKeyConfigProperties;
    }

    @PostConstruct
    public void init() {
        try {
            rsaKeyConfigProperties.setPublicKey(loadPublicKey());
            rsaKeyConfigProperties.setPrivateKey(loadPrivateKey());
        } catch (JwtDecodingException e) {
            log.error("Failed to load RSA keys", e);
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

    private RSAPrivateKey loadPrivateKey() throws JwtDecodingException {
        try {
            byte[] keyBytes = privateKeyResource.getInputStream().readAllBytes();
            String privateKeyContent = new String(keyBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            log.info("Private key content: {}", privateKeyContent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new JwtDecodingException("Failed to load the private key", e);
        }
    }
}