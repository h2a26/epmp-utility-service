package org.mpay.utilityservice.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PepperedPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();
    private final String secretKey;

    public PepperedPasswordEncoder(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        // Step 1: Generate HMAC-SHA256 signature (Pepper)
        String peppered = generateSignature(rawPassword.toString());
        // Step 2: Hash that signature with BCrypt
        return delegate.encode(peppered);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Step 1: Pepper the incoming login password
        String peppered = generateSignature(rawPassword.toString());
        // Step 2: Compare against the BCrypt hash in the DB
        return delegate.matches(peppered, encodedPassword);
    }

    /**
     * Utility method to generate HMAC-SHA256 signatures using the system secret.
     */
    private String generateSignature(String data) {
        try {
            final String algorithm = "HmacSHA256";
            Mac sha256_HMAC = Mac.getInstance(algorithm);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
            sha256_HMAC.init(secret_key);
            return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error generating internal signature", e);
        }
    }
}