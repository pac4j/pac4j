package org.pac4j.openid4vp.profile;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.dcql.DcqlQuery;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.verifier.VerifiedCredential;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the profile identifier made of the issuer and a claim, and its check against the DCQL query.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class IssuerAndClaimProfileIdResolverTests {

    private final ProfileIdResolver resolver = ProfileIdResolver.issuerAndClaim("sub");

    @Test
    void testIdentityIsStableAndScopedToTheIssuer() {
        val id = resolver.resolve(credentials("issuer-a", Map.of("sub", "alice")));
        assertEquals(id, resolver.resolve(credentials("issuer-a", Map.of("sub", "alice", "name", "Alice"))));
        assertNotEquals(id, resolver.resolve(credentials("issuer-b", Map.of("sub", "alice"))));
        assertNotEquals(id, resolver.resolve(credentials("issuer-a", Map.of("sub", "bob"))));
        assertNotEquals(resolver.resolve(credentials("a.b", Map.of("sub", "c"))),
            resolver.resolve(credentials("a", Map.of("sub", "b.c"))));
    }

    @Test
    void testMissingOrInvalidIdentifiersAreRejected() {
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(credentials(null, Map.of("sub", "alice"))));
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(credentials(" ", Map.of("sub", "alice"))));
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(credentials("issuer", Map.of())));
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(credentials("issuer", Map.of("sub", " "))));
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(credentials("issuer", Map.of("sub", 42))));
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(new VerifiablePresentationCredentials()));
    }

    @Test
    void testMultipleCredentialsAreRejectedWithoutAnExplicitSelection() {
        val credentials = credentials("issuer", Map.of("sub", "alice"));
        val credential = credentials.getVerifiedCredentials().get("pid").get(0);
        credentials.getVerifiedCredentials().put("pid", List.of(credential, credential));
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(credentials));
        credentials.getVerifiedCredentials().put("pid", List.of(credential));
        credentials.getVerifiedCredentials().put("other", List.of(credential));
        assertThrows(OpenId4VpException.class, () -> resolver.resolve(credentials));
    }

    @Test
    void testTheSubjectClaimCanBeChosen() {
        val expected = resolver.resolve(credentials("issuer", Map.of("sub", "alice")));
        assertEquals(expected, ProfileIdResolver.issuerAndClaim("account_id")
            .resolve(credentials("issuer", Map.of("account_id", "alice"))));
        assertThrows(TechnicalException.class, () -> ProfileIdResolver.issuerAndClaim(" "));
    }

    @Test
    void testTheQueryMustBeAbleToReturnTheClaim() {
        // no claims listed: the wallet returns the claims that are mandatory to present, which may include sub
        assertDoesNotThrow(() -> resolver.check(DcqlQuery.parse(query(""))));
        assertDoesNotThrow(() -> resolver.check(DcqlQuery.parse(query(",\"claims\":[{\"path\":[\"sub\"]}]"))));
        val error = assertThrows(TechnicalException.class,
            () -> resolver.check(DcqlQuery.parse(query(",\"claims\":[{\"path\":[\"given_name\"]}]"))));
        assertEquals("no SD-JWT VC credential query of the DCQL query can return the claim sub used as profile identifier: "
            + "request it, or configure another profileIdResolver", error.getMessage());
        // a nested path is not the top-level claim this resolver reads
        assertThrows(TechnicalException.class,
            () -> resolver.check(DcqlQuery.parse(query(",\"claims\":[{\"path\":[\"address\",\"sub\"]}]"))));
    }

    @Test
    void testAMobileDocumentNeverReturnsATopLevelClaim() {
        val mdoc = DcqlQuery.parse("{\"credentials\":[{\"id\":\"mdl\",\"format\":\"mso_mdoc\","
            + "\"meta\":{\"doctype_value\":\"org.iso.18013.5.1.mDL\"}}]}");
        assertThrows(TechnicalException.class, () -> resolver.check(mdoc));
    }

    @Test
    void testACustomResolverChecksNothingByDefault() {
        ProfileIdResolver custom = credentials -> "id";
        assertDoesNotThrow(() -> custom.check(DcqlQuery.parse(query(",\"claims\":[{\"path\":[\"given_name\"]}]"))));
    }

    private static String query(final String claims) {
        return "{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\",\"meta\":{\"vct_values\":[\"urn:eudi:pid:1\"]}"
            + claims + "}]}";
    }

    private static VerifiablePresentationCredentials credentials(final String issuer, final Map<String, Object> claims) {
        val credentials = new VerifiablePresentationCredentials();
        credentials.getVerifiedCredentials().put("pid", List.of(new VerifiedCredential().setIssuer(issuer).setClaims(claims)));
        return credentials;
    }
}
