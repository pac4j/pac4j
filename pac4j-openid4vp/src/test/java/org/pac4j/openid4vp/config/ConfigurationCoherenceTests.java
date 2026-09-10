package org.pac4j.openid4vp.config;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.openid4vp.dcql.DcqlQuery;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.exception.TechnicalException;
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
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}")
            .setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"))
            .setResponseMode(ResponseMode.DIRECT_POST_JWT);
        configuration.setExpectedOrigins(List.of("https://app.example.org"));

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "the response mode of a digital credentials API request must be dc_api or dc_api.jwt");
    }

    @Test
    void testEveryQueriedFormatNeedsAVerifier() {
        val configuration = valid();
        // a mobile document is asked for, but only the SD-JWT VC verifier is registered
        configuration.setDcqlQuery("{\"credentials\":[{\"id\":\"mdl\",\"format\":\"mso_mdoc\"}]}");

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "credentialVerifier for the format mso_mdoc of the credential query mdl cannot be null");
    }

    @Test
    void testAMalformedDcqlQueryIsRefusedWhenSet() {
        val e = TestsHelper.expectException(() -> valid().setDcqlQuery("{\"credentials\": ["));
        org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().startsWith("dcqlQuery is not a JSON object: "), e.getMessage());
    }

    @Test
    void testAnEmptyDcqlQueryIsRefusedAtInitialization() {
        val configuration = valid().setDcqlQuery("{\"credentials\":[]}");

        TestsHelper.expectException(configuration::init, TechnicalException.class, "credentials cannot be empty");
    }

    @Test
    void testEitherADcqlQueryOrAScopeButNotBoth() {
        val both = valid().setScope("com.example.pid_presentation");
        TestsHelper.expectException(both::init, TechnicalException.class, "either dcqlQuery or scope must be defined, but not both");

        val neither = valid().setDcqlQuery((DcqlQuery) null);
        TestsHelper.expectException(neither::init, TechnicalException.class, "either dcqlQuery or scope must be defined, but not both");

        val scopeOnly = valid().setDcqlQuery((DcqlQuery) null).setScope("com.example.pid_presentation");
        scopeOnly.init();
    }

    @Test
    void testAVerifierAttestationNeedsAFormatAndData() {
        val noFormat = valid();
        noFormat.getVerifierInfo().add(new VerifierAttestation(null, "eyJ..."));
        TestsHelper.expectException(noFormat::init, TechnicalException.class, "format cannot be blank");

        val noData = valid();
        noData.getVerifierInfo().add(new VerifierAttestation("jwt", null));
        TestsHelper.expectException(noData::init, TechnicalException.class, "data cannot be null");

        val wrongData = valid();
        wrongData.getVerifierInfo().add(new VerifierAttestation("jwt", 42));
        TestsHelper.expectException(wrongData::init, TechnicalException.class, "data must be a string or a JSON object");
    }

    @Test
    void testTheRequestUriMethodIsMandatory() {
        val configuration = valid().setRequestUriMethod(null);

        TestsHelper.expectException(configuration::init, TechnicalException.class, "requestUriMethod cannot be null");
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
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}")
            .setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        // the browser origin never has a path: this value could never match it
        configuration.setExpectedOrigins(List.of("https://app.example.org/login"));

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "an expected origin must be a scheme, a host and an optional port, nothing more: https://app.example.org/login");
    }
}
