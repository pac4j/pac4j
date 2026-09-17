package org.pac4j.openid4vp.dcql;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.verifier.VerifiedCredential;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DCQL matching on verified evidence, including alternatives and nested paths.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class DcqlValidatorTests {

    private final DcqlValidator validator = new DcqlValidator();

    private CredentialQuery query() {
        return new CredentialQuery("pid", CredentialFormat.SD_JWT_VC).setVctValues("urn:pid");
    }

    private VerifiedCredential credential(final Map<String, Object> claims) {
        return new VerifiedCredential().setFormat(CredentialFormat.SD_JWT_VC).setType("urn:pid")
            .setCryptographicHolderBinding(true).setClaims(claims);
    }

    @Test
    void requiresVerifiedFormatTypeAndHolderBinding() {
        val query = query();
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query, null));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query, credential(Map.of()).setFormat(CredentialFormat.MSO_MDOC)));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query, credential(Map.of()).setType("urn:other")));
        val unbound = credential(Map.of()).setCryptographicHolderBinding(false);
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query, unbound));
        query.setRequireCryptographicHolderBinding(false);
        assertDoesNotThrow(() -> validator.validateCredential(query, unbound));
    }

    @Test
    void checksNestedPathsArrayIndicesAndWildcards() {
        val query = query().addClaim("address", "city").addClaim("nationalities", 1)
            .addClaim(new ClaimsQuery("degrees", null, "name").withValues("MSc"));
        val claims = Map.<String, Object>of("address", Map.of("city", "Paris"), "nationalities", List.of("FR", "BE"),
            "degrees", List.of(Map.of("name", "BSc"), Map.of("name", "MSc")));
        assertDoesNotThrow(() -> validator.validateCredential(query, credential(claims)));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query().addClaim("nationalities", 2), credential(claims)));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query().addClaim("address", null), credential(claims)));
    }

    @Test
    void wildcardRejectsWrongContainersButSkipsMissingMembers() {
        val query = query().addClaim("items", null, "name");
        assertDoesNotThrow(() -> validator.validateCredential(query,
            credential(Map.of("items", List.of(Map.of("name", "Alice"), Map.of("other", "Bob"))))));
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query,
            credential(Map.of("items", List.of(Map.of("name", "Alice"), "not-an-object")))));
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query,
            credential(Map.of("items", List.of()))));
    }

    @Test
    void distinguishesPresentNullFromMissingClaim() {
        val claims = new LinkedHashMap<String, Object>();
        claims.put("name", null);
        val query = query().addClaim("name");
        assertDoesNotThrow(() -> validator.validateCredential(query, credential(claims)));
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query, credential(Map.of())));
    }

    @Test
    void checksValuesWithoutStringOrBooleanCoercion() {
        val query = query().addClaim(new ClaimsQuery("age").withValues(18))
            .addClaim(new ClaimsQuery("adult").withValues(true));
        assertDoesNotThrow(() -> validator.validateCredential(query, credential(Map.of("age", 18L, "adult", true))));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query, credential(Map.of("age", "18", "adult", true))));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query, credential(Map.of("age", 18, "adult", "true"))));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query, credential(Map.of("age", 19, "adult", true))));
    }

    @Test
    void acceptsOneCompleteClaimSetButNotPartsFromDifferentOptions() {
        val query = query()
            .addClaim(new ClaimsQuery("adult").setId("adult").withValues(true))
            .addClaim(new ClaimsQuery("birth_date").setId("birth"))
            .addClaim(new ClaimsQuery("name").setId("name"))
            .setClaimSets(List.of(List.of("adult"), List.of("birth", "name")));
        assertDoesNotThrow(() -> validator.validateCredential(query, credential(Map.of("adult", true))));
        assertDoesNotThrow(() -> validator.validateCredential(query,
            credential(Map.of("birth_date", "2000-01-01", "name", "Alice"))));
        assertThrows(OpenId4VpException.class,
            () -> validator.validateCredential(query, credential(Map.of("adult", false, "name", "Alice"))));
    }

    @Test
    void checksMdocNamespaceAndDocumentType() {
        val query = new CredentialQuery("mdl", CredentialFormat.MSO_MDOC).setDoctypeValue("org.iso.mDL")
            .addClaim("org.iso", "family_name");
        val credential = credential(Map.of("org.iso", Map.of("family_name", "Doe")))
            .setFormat(CredentialFormat.MSO_MDOC).setType("org.iso.mDL");
        assertDoesNotThrow(() -> validator.validateCredential(query, credential));
        credential.setClaims(Map.of("family_name", "Doe"));
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query, credential));
        credential.setClaims(Map.of("org.iso", Map.of("family_name", "Doe"))).setType("other");
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query, credential));
    }

    @Test
    void trustsOnlyAuthorityEvidenceProvidedByTheVerifier() {
        val query = query().addTrustedAuthority(new TrustedAuthority(TrustedAuthority.AKI, "trusted-key"))
            .addTrustedAuthority(new TrustedAuthority(TrustedAuthority.ETSI_TL, "https://trusted.example"));
        val credential = credential(Map.of("aki", "trusted-key"));
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query, credential));
        credential.setTrustedAuthorities(Map.of(TrustedAuthority.AKI, List.of("other-key")));
        assertThrows(OpenId4VpException.class, () -> validator.validateCredential(query, credential));
        credential.setTrustedAuthorities(Map.of(TrustedAuthority.ETSI_TL, List.of("https://trusted.example")));
        assertDoesNotThrow(() -> validator.validateCredential(query, credential));
    }

    @Test
    void requiresAllCredentialsWithoutSets() {
        val query = new DcqlQuery().addCredential(query()).addCredential(query().setId("mdl"));
        assertThrows(OpenId4VpException.class, () -> validator.validateSelection(query, Set.of("pid")));
        assertThrows(OpenId4VpException.class, () -> validator.validateSelection(query, Set.of()));
        assertDoesNotThrow(() -> validator.validateSelection(query, Set.of("pid", "mdl")));
    }

    @Test
    void requiresAllRequiredSetsWithOneCompleteOptionEach() {
        val query = new DcqlQuery().addCredential(query()).addCredential(query().setId("mdl"))
            .addCredential(query().setId("address"))
            .addCredentialSet(new CredentialSetQuery().addOption("pid", "address").addOption("mdl"))
            .addCredentialSet(new CredentialSetQuery().addOption("address").setRequired(false));
        assertDoesNotThrow(() -> validator.validateSelection(query, Set.of("mdl")));
        assertDoesNotThrow(() -> validator.validateSelection(query, Set.of("pid", "address")));
        assertThrows(OpenId4VpException.class, () -> validator.validateSelection(query, Set.of("pid")));
        query.addCredentialSet(new CredentialSetQuery().addOption("address"));
        assertThrows(OpenId4VpException.class, () -> validator.validateSelection(query, Set.of("mdl")));
    }

    @Test
    void rejectsInvalidQueryPathsValuesAndMetadata() {
        for (val path : List.of(List.of("items", -1), List.of("items", 0.5), List.of("items", true))) {
            assertThrows(TechnicalException.class,
                () -> query().addClaim(new ClaimsQuery().setPath(new java.util.ArrayList<>(path))).check());
        }
        for (val index : List.of("0.5", "4294967296", "-1")) {
            assertThrows(TechnicalException.class, () -> DcqlQuery.parse(
                "{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\",\"meta\":{\"vct_values\":[\"urn:pid\"]},"
                    + "\"claims\":[{\"path\":[\"items\","
                    + index + "]}]}]}").check());
        }
        assertThrows(TechnicalException.class, () -> query().addClaim(new ClaimsQuery("x").withValues(0.5)).check());
        assertThrows(TechnicalException.class, () -> query().setVctValues().check());
        assertThrows(TechnicalException.class, () -> query().setMeta(Map.of("unsupported", true)).check());
        assertThrows(TechnicalException.class, () -> new CredentialQuery("mdl", CredentialFormat.MSO_MDOC)
            .setDoctypeValue("org.iso.mDL").addClaim(new ClaimsQuery().setPath(Arrays.asList("namespace", null))).check());
    }
}
