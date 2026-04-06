package com.meevent.webapi.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Primary
@Profile("dev")
public class DevGoogleTokenVerifier extends GoogleTokenVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevGoogleTokenVerifier.class);

    @Override
    public GoogleIdToken.Payload verify(String idTokenString) {
        // In dev profile, accept a special test token format: "dev-test-{email}"
        if (idTokenString != null && idTokenString.startsWith("dev-test-")) {
            String email = idTokenString.substring("dev-test-".length());
            LOGGER.info("DEV MODE: Accepting test token for email={}", email);

            GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
            payload.setSubject("google-sub-" + email.hashCode());
            payload.setEmail(email);
            payload.setEmailVerified(true);
            payload.set("name", "Test User " + email.split("@")[0]);
            return payload;
        }

        // For non-test tokens, fall through to real verification (will fail with dummy client ID)
        return super.verify(idTokenString);
    }
}
