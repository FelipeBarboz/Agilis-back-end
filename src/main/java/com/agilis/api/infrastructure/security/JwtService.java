package com.agilis.api.infrastructure.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class JwtService {

    private final DefaultJWTProcessor<SecurityContext> jwtProcessor;

    public JwtService(
            @Value("${supabase.jwks.url}") String jwksUrl,
            @Value("${supabase.anon.key}") String anonKey
    ) throws Exception {

        DefaultResourceRetriever retriever = new DefaultResourceRetriever(5000, 5000) {
            @Override
            public com.nimbusds.jose.util.Resource retrieveResource(URL url) throws java.io.IOException {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestProperty("apikey", anonKey);
                conn.setRequestProperty("Authorization", "Bearer " + anonKey);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() != 200) {
                    throw new java.io.IOException("JWKS request failed: HTTP " + conn.getResponseCode());
                }

                String content = new String(conn.getInputStream().readAllBytes());
                return new com.nimbusds.jose.util.Resource(content, "application/json");
            }
        };

        JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(jwksUrl), retriever);

        JWSVerificationKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.ES256, jwkSource);

        this.jwtProcessor = new DefaultJWTProcessor<>();
        this.jwtProcessor.setJWSKeySelector(keySelector);
    }

    public String extractUserId(String token) {
        try {
            JWTClaimsSet claims = jwtProcessor.process(token, null);
            System.out.println("JWT OK - userId: " + claims.getSubject());
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("JWT ERRO: " + e.getClass().getName() + " - " + e.getMessage());
            throw new RuntimeException("Token inválido ou expirado", e);
        }
    }

    public boolean isValid(String token) {
        try {
            extractUserId(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}