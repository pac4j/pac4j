package org.pac4j.openid4vp.dcql;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.util.TestsHelper;
import org.pac4j.openid4vp.config.CredentialFormat;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.*;

/**
 * Tests the DCQL query model: what it writes, what it reads back, and what it refuses.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class DcqlQueryTests {

    @Test
    void testAQueryBuiltProgrammaticallyWritesTheJsonOfTheSpecification() {
        val query = new DcqlQuery()
            .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC)
                .setVctValues("urn:eudi:pid:1")
                .addClaim("given_name")
                .addClaim(new ClaimsQuery("age_over_18").withValues(true))
                .addTrustedAuthority(new TrustedAuthority(TrustedAuthority.ETSI_TL, "https://lotl.example.com")))
            .addCredential(new CredentialQuery("mdl", CredentialFormat.MSO_MDOC)
                .setDoctypeValue("org.iso.18013.5.1.mDL")
                .addClaim("org.iso.18013.5.1", "family_name")
                .setMultiple(true))
            .addCredentialSet(new CredentialSetQuery().addOption("pid").addOption("mdl").setRequired(true).setPurpose("age check"));
        query.check();

        val json = query.toJson();
        val credentials = (List<Map<String, Object>>) json.get(DcqlQuery.CREDENTIALS);
        assertEquals(2, credentials.size());
        assertEquals(Map.of("id", "pid", "format", "dc+sd-jwt", "meta", Map.of("vct_values", List.of("urn:eudi:pid:1")),
            "trusted_authorities", List.of(Map.of("type", "etsi_tl", "values", List.of("https://lotl.example.com"))),
            "claims", List.of(Map.of("path", List.of("given_name")), Map.of("path", List.of("age_over_18"), "values", List.of(true)))),
            credentials.get(0));
        assertEquals(Map.of("id", "mdl", "format", "mso_mdoc", "multiple", true, "meta", Map.of("doctype_value", "org.iso.18013.5.1.mDL"),
            "claims", List.of(Map.of("path", List.of("org.iso.18013.5.1", "family_name")))), credentials.get(1));
        assertEquals(List.of(Map.of("options", List.of(List.of("pid"), List.of("mdl")), "required", true, "purpose", "age check")),
            json.get(DcqlQuery.CREDENTIAL_SETS));
        // the meta member is always there, "if empty, no specific constraints are placed"
        assertEquals(Map.of(), new CredentialQuery("x", CredentialFormat.SD_JWT_VC).toJson().get(CredentialQuery.META));
    }

    @Test
    void testAQueryReadsBackWhatItWrote() {
        val query = new DcqlQuery()
            .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC)
                .setVctValues("urn:eudi:pid:1")
                .setRequireCryptographicHolderBinding(false)
                .addClaim(new ClaimsQuery("address", "street").setId("street"))
                .addClaim(new ClaimsQuery("nationalities", null).setId("nationalities").withValues("FR", "DE"))
                .setClaimSets(List.of(List.of("street"), List.of("street", "nationalities"))))
            .addCredentialSet(new CredentialSetQuery().addOption("pid").setRequired(false));
        query.check();

        val parsed = DcqlQuery.parse(query.toJsonString());
        parsed.check();
        assertEquals(query.toJson(), parsed.toJson());
        val pid = parsed.findCredential("pid").get();
        assertEquals(CredentialFormat.SD_JWT_VC, pid.getFormat());
        assertEquals(List.of("urn:eudi:pid:1"), pid.getMeta().get(CredentialQuery.VCT_VALUES));
        assertEquals(Boolean.FALSE, pid.getRequireCryptographicHolderBinding());
        // a null path segment stands for every element of an array
        assertEquals(java.util.Arrays.asList("nationalities", null), pid.getClaims().get(1).getPath());
        assertEquals(List.of("FR", "DE"), pid.getClaims().get(1).getValues());
        assertTrue(parsed.findCredential("unknown").isEmpty());
    }

    @Test
    void testTheRulesOfTheSpecificationAreEnforced() {
        // "the same id MUST NOT be present more than once"
        val duplicate = new DcqlQuery()
            .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC))
            .addCredential(new CredentialQuery("pid", CredentialFormat.MSO_MDOC));
        TestsHelper.expectException(duplicate::check, TechnicalException.class, "duplicate credential query identifier: pid");

        // "alphanumeric, underscore (_), or hyphen (-) characters"
        val badId = new DcqlQuery().addCredential(new CredentialQuery("my pid", CredentialFormat.SD_JWT_VC));
        TestsHelper.expectException(badId::check, TechnicalException.class,
            "a credential query identifier must be a non-empty string of alphanumeric, underscore or hyphen characters: my pid");

        // "identifiers which reference elements in credentials"
        val unknownOption = new DcqlQuery()
            .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC))
            .addCredentialSet(new CredentialSetQuery().addOption("mdl"));
        TestsHelper.expectException(unknownOption::check, TechnicalException.class,
            "unknown credential query identifier in a credential set: mdl");

        // "id: REQUIRED if claim_sets is present in the Credential Query"
        val claimSetsWithoutIds = new DcqlQuery()
            .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC)
                .addClaim("given_name")
                .setClaimSets(List.of(List.of("given_name"))));
        TestsHelper.expectException(claimSetsWithoutIds::check, TechnicalException.class,
            "a claim identifier is required when the credential query has claim sets: [given_name]");

        // a path is never empty
        val emptyPath = new DcqlQuery().addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC).addClaim(new ClaimsQuery()));
        TestsHelper.expectException(emptyPath::check, TechnicalException.class, "a claim query path cannot be empty");
    }

    @Test
    void testAMalformedJsonIsRefusedWithItsMember() {
        TestsHelper.expectException(() -> DcqlQuery.parse("{\"credentials\":{}}"), TechnicalException.class,
            "a JSON array was expected for the DCQL member credentials, got: {}");
        TestsHelper.expectException(() -> DcqlQuery.parse("{\"credentials\":[{\"id\":\"pid\",\"format\":\"unknown\"}]}"),
            TechnicalException.class, "unsupported credential format in the DCQL query: unknown");
        val e = TestsHelper.expectException(() -> DcqlQuery.parse("not json"));
        assertTrue(e.getMessage().startsWith("dcqlQuery is not a JSON object: "), e.getMessage());
    }

    @Test
    void testTheEudiPidQueries() {
        val sdJwt = EudiPidQuery.sdJwtVc(GIVEN_NAME, AGE_OVER_18);
        sdJwt.check();
        assertEquals("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\",\"meta\":{\"vct_values\":[\"urn:eudi:pid:1\"]},"
            + "\"claims\":[{\"path\":[\"given_name\"]},{\"path\":[\"age_over_18\"]}]}]}", sdJwt.toJsonString());

        val mdoc = EudiPidQuery.mdoc(GIVEN_NAME);
        mdoc.check();
        // the namespace of the PID is its document type
        assertEquals("{\"credentials\":[{\"id\":\"pid\",\"format\":\"mso_mdoc\",\"meta\":{\"doctype_value\":\"eu.europa.ec.eudi.pid.1\"},"
            + "\"claims\":[{\"path\":[\"eu.europa.ec.eudi.pid.1\",\"given_name\"]}]}]}", mdoc.toJsonString());
    }
}
