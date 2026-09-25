package org.pac4j.openid4vp.profile;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.profile.creator.OpenId4VpProfileCreator;
import org.pac4j.openid4vp.verifier.VerifiedCredential;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests identity mapping and delegation to profile definitions.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpProfileDefinitionTests {

    @Test
    void testIdentityIsStableAndScopedToTheIssuer() {
        val definition = new OpenId4VpProfileDefinition();
        val id = definition.newProfile(credentials("issuer-a", Map.of("sub", "alice"))).getId();
        assertEquals(id, definition.newProfile(credentials("issuer-a", Map.of("sub", "alice", "name", "Alice"))).getId());
        assertNotEquals(id, definition.newProfile(credentials("issuer-b", Map.of("sub", "alice"))).getId());
        assertNotEquals(id, definition.newProfile(credentials("issuer-a", Map.of("sub", "bob"))).getId());
        assertNotEquals(definition.newProfile(credentials("a.b", Map.of("sub", "c"))).getId(),
            definition.newProfile(credentials("a", Map.of("sub", "b.c"))).getId());
    }

    @Test
    void testMissingOrInvalidIdentifiersAreRejected() {
        val definition = new OpenId4VpProfileDefinition();
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(credentials(null, Map.of("sub", "alice"))));
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(credentials(" ", Map.of("sub", "alice"))));
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(credentials("issuer", Map.of())));
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(credentials("issuer", Map.of("sub", " "))));
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(credentials("issuer", Map.of("sub", 42))));
    }

    @Test
    void testVerifiedCredentialsAreRequired() {
        val definition = new OpenId4VpProfileDefinition();
        assertThrows(OpenId4VpException.class, definition::newProfile);
        assertThrows(OpenId4VpException.class, () -> definition.newProfile("unverified"));
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(new VerifiablePresentationCredentials()));
    }

    @Test
    void testMultipleCredentialsAreRejectedWithoutAnExplicitSelection() {
        val definition = new OpenId4VpProfileDefinition();
        val credentials = credentials("issuer", Map.of("sub", "alice"));
        val credential = credentials.getVerifiedCredentials().get("pid").get(0);
        credentials.getVerifiedCredentials().put("pid", List.of(credential, credential));
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(credentials));
        credentials.getVerifiedCredentials().put("pid", List.of(credential));
        credentials.getVerifiedCredentials().put("other", List.of(credential));
        assertThrows(OpenId4VpException.class, () -> definition.newProfile(credentials));
    }

    @Test
    void testTheSubjectClaimCanBeConfigured() {
        val definition = new OpenId4VpProfileDefinition();
        val expected = definition.newProfile(credentials("issuer", Map.of("sub", "alice"))).getId();
        definition.setProfileId("account_id");
        assertEquals(expected, definition.newProfile(credentials("issuer", Map.of("account_id", "alice"))).getId());
    }

    @Test
    void testTheFactoryReceivesTheCredentials() {
        val credentials = credentials("issuer", Map.of("sub", "alice"));
        val definition = new OpenId4VpProfileDefinition(parameters -> {
            assertSame(credentials, parameters[0]);
            return new EudiPidProfile();
        });
        val profile = definition.newProfile(credentials);
        assertInstanceOf(EudiPidProfile.class, profile);
        assertNotNull(profile.getId());
    }

    @Test
    void testTheCreatorDelegatesIdentityAndPreservesAttributesFromAllCredentials() {
        val credentials = credentials("issuer", Map.of("given_name", "Alice"));
        credentials.getVerifiedCredentials().put("age", List.of(new VerifiedCredential().setClaims(Map.of("age_over_18", true))));
        val definition = new CommonProfileDefinition(parameters -> {
            assertSame(credentials, parameters[0]);
            val profile = new VerifiableCredentialProfile();
            profile.setId("application-account-id");
            return profile;
        });
        val client = new OpenId4VpClient();
        client.setName("wallet");
        val creator = new OpenId4VpProfileCreator(client, definition);
        val profile = creator.create(null, credentials).orElseThrow();
        assertEquals("application-account-id", profile.getId());
        assertEquals("Alice", profile.getAttribute("given_name"));
        assertEquals(true, profile.getAttribute("age_over_18"));
        assertEquals("wallet", profile.getClientName());
        assertTrue(creator.create(null, new VerifiablePresentationCredentials()).isEmpty());
    }

    private VerifiablePresentationCredentials credentials(final String issuer, final Map<String, Object> claims) {
        val credentials = new VerifiablePresentationCredentials();
        credentials.getVerifiedCredentials().put("pid", List.of(new VerifiedCredential().setIssuer(issuer).setClaims(claims)));
        return credentials;
    }
}
