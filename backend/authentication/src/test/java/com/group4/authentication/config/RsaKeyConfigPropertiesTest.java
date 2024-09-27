package com.group4.authentication.config;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

class RsaKeyConfigPropertiesTest {

    @Test
    void testPublicKeySetterGetter() {
        RsaKeyConfigProperties rsaKeyConfigProperties = new RsaKeyConfigProperties();
        RSAPublicKey publicKey = mock(RSAPublicKey.class);
        rsaKeyConfigProperties.setPublicKey(publicKey);
        assertThat(rsaKeyConfigProperties.getPublicKey()).isEqualTo(publicKey);
    }

    @Test
    void testPrivateKeySetterGetter() {
        RsaKeyConfigProperties rsaKeyConfigProperties = new RsaKeyConfigProperties();
        RSAPrivateKey privateKey = mock(RSAPrivateKey.class);
        rsaKeyConfigProperties.setPrivateKey(privateKey);
        assertThat(rsaKeyConfigProperties.getPrivateKey()).isEqualTo(privateKey);
    }
}

