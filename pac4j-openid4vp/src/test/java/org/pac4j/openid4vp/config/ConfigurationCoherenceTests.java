package org.pac4j.openid4vp.config;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.test.util.TestsHelper;

import java.nio.file.Path;
import java.util.List;

/**
 * Tests the coherence checks of the configuration: what one field allows given another, refused at
 * initialization rather than discovered at the first request.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class ConfigurationCoherenceTests {

    @TempDir
    private Path directory;

    private OpenId4VpConfiguration valid() {
        val configuration = new OpenId4VpConfiguration();
        configuration.setClientId("did:example:123")
            .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}")
            .setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.addCredentialVerifier(new SdJwtVcVerifier());
        return configuration;
    }

    @Test
    void testADcApiResponseModeIsRefusedOnTheUrlBinding() {
        val configuration = valid().setResponseMode(ResponseMode.DC_API_JWT);

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "the dc_api.jwt response mode belongs to the digital credentials API: a wallet invoked by a URL posts its "
                + "answer, use direct_post.jwt");
    }

    @Test
    void testADirectPostResponseModeIsRefusedOnTheDcApiBinding() {
        val configuration = new OpenId4VpDcApiConfiguration();
        configuration.setClientId("did:example:123")
            .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
            .setDcqlQuery("{\"credentials\":[]}")
            .setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"))
            .setResponseMode(ResponseMode.DIRECT_POST_JWT);
        configuration.setExpectedOrigins(List.of("https://app.example.org"));
        configuration.addCredentialVerifier(new SdJwtVcVerifier());

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "the response mode of a digital credentials API request must be dc_api or dc_api.jwt");
    }

    @Test
    void testAMalformedDcqlQueryIsRefusedAtInitialization() {
        val configuration = valid().setDcqlQuery("{\"credentials\": [");

        val e = TestsHelper.expectException(configuration::init);
        org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().startsWith("dcqlQuery is not a JSON object: "), e.getMessage());
    }

    @Test
    void testTheTransactionLifetimeMustBePositive() {
        val configuration = valid().setTransactionLifetimeSeconds(0);

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "transactionLifetimeSeconds must be greater than zero");
    }

    @Test
    void testAnExpectedOriginCannotCarryAPath() {
        val configuration = new OpenId4VpDcApiConfiguration();
        configuration.setClientId("did:example:123")
            .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
            .setDcqlQuery("{\"credentials\":[]}")
            .setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        // the browser origin never has a path: this value could never match it
        configuration.setExpectedOrigins(List.of("https://app.example.org/login"));
        configuration.addCredentialVerifier(new SdJwtVcVerifier());

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "an expected origin must be a scheme, a host and an optional port, nothing more: https://app.example.org/login");
    }
}
