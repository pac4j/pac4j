package org.pac4j.openid4vp.dcql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.openid4vp.config.CredentialFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * A credential query: one credential the verifier asks for, of one format, with the claims it wants from it.
 *
 * <p>The {@code meta} member is defined per format: {@code vct_values} for a SD-JWT VC, {@code doctype_value}
 * for a mobile document, set with {@link #setVctValues(String...)} and {@link #setDoctypeValue(String)}.
 * The specification has it required for those formats; it is not enforced here, so that a query written
 * for a wallet which does not care still goes through, but a real wallet is likely to refuse a query
 * without it.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#credential_query">
 *     OpenID4VP 1.0, credential query</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class CredentialQuery {

    /** JSON member names. */
    public static final String ID = "id";
    public static final String FORMAT = "format";
    public static final String MULTIPLE = "multiple";
    public static final String META = "meta";
    public static final String TRUSTED_AUTHORITIES = "trusted_authorities";
    public static final String REQUIRE_CRYPTOGRAPHIC_HOLDER_BINDING = "require_cryptographic_holder_binding";
    public static final String CLAIMS = "claims";
    public static final String CLAIM_SETS = "claim_sets";
    /** The meta member of a SD-JWT VC query. */
    public static final String VCT_VALUES = "vct_values";
    /** The meta member of a mobile document query. */
    public static final String DOCTYPE_VALUE = "doctype_value";

    /** "A non-empty string consisting of alphanumeric, underscore (_), or hyphen (-) characters." */
    static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z0-9_-]+");

    /** The identifier of this query, which indexes the presentations in the response. */
    private String id;

    private CredentialFormat format;

    /** "Whether multiple Credentials can be returned for this Credential Query", false when absent. */
    private Boolean multiple;

    /** The format-specific constraints on the credential's metadata, an empty object when there are none. */
    private Map<String, Object> meta = new LinkedHashMap<>();

    private List<TrustedAuthority> trustedAuthorities = new ArrayList<>();

    /** "Whether the Verifier requires a Cryptographic Holder Binding proof", true when absent. */
    private Boolean requireCryptographicHolderBinding;

    private List<ClaimsQuery> claims = new ArrayList<>();

    /** "Which combinations of claims for the Credential are requested", each as a list of claim identifiers. */
    private List<List<String>> claimSets = new ArrayList<>();

    /**
     * <p>Constructor for CredentialQuery.</p>
     *
     * @param id the identifier
     * @param format the format
     */
    public CredentialQuery(final String id, final CredentialFormat format) {
        this.id = id;
        this.format = format;
    }

    /**
     * <p>The allowed types of a SD-JWT VC, the {@code vct_values} of its meta.</p>
     *
     * @param vctValues the allowed types
     * @return this query
     */
    public CredentialQuery setVctValues(final String... vctValues) {
        meta.put(VCT_VALUES, Arrays.asList(vctValues));
        return this;
    }

    /**
     * <p>The allowed document type of a mobile document, the {@code doctype_value} of its meta.</p>
     *
     * @param doctypeValue the document type
     * @return this query
     */
    public CredentialQuery setDoctypeValue(final String doctypeValue) {
        meta.put(DOCTYPE_VALUE, doctypeValue);
        return this;
    }

    /**
     * <p>Ask for a claim, by its path.</p>
     *
     * @param path the claims path pointer, one segment per element
     * @return this query
     */
    public CredentialQuery addClaim(final Object... path) {
        claims.add(new ClaimsQuery(path));
        return this;
    }

    /**
     * <p>Ask for a claim.</p>
     *
     * @param claim the claim query
     * @return this query
     */
    public CredentialQuery addClaim(final ClaimsQuery claim) {
        claims.add(claim);
        return this;
    }

    /**
     * <p>Accept the credentials of an authority.</p>
     *
     * @param trustedAuthority the authority
     * @return this query
     */
    public CredentialQuery addTrustedAuthority(final TrustedAuthority trustedAuthority) {
        trustedAuthorities.add(trustedAuthority);
        return this;
    }

    /**
     * <p>Check this query against the rules of the specification.</p>
     */
    public void check() {
        assertTrue(id != null && IDENTIFIER.matcher(id).matches(),
            "a credential query identifier must be a non-empty string of alphanumeric, underscore or hyphen characters: " + id);
        assertNotNull("format of the credential query " + id, format);
        assertNotNull("meta of the credential query " + id, meta);
        if (trustedAuthorities != null) {
            trustedAuthorities.forEach(TrustedAuthority::check);
        }
        val claimIds = new HashSet<String>();
        val hasClaimSets = claimSets != null && !claimSets.isEmpty();
        if (claims != null) {
            for (val claim : claims) {
                claim.check(hasClaimSets);
                // "Within the particular claims array, the same id MUST NOT be present more than once"
                assertTrue(claim.getId() == null || claimIds.add(claim.getId()),
                    "duplicate claim identifier in the credential query " + id + ": " + claim.getId());
            }
        }
        if (hasClaimSets) {
            assertTrue(claims != null && !claims.isEmpty(), "claim sets without any claim in the credential query " + id);
            for (val claimSet : claimSets) {
                assertTrue(claimSet != null && !claimSet.isEmpty(), "a claim set cannot be empty in the credential query " + id);
                for (val claimId : claimSet) {
                    assertTrue(claimIds.contains(claimId), "unknown claim identifier in a claim set of the credential query "
                        + id + ": " + claimId);
                }
            }
        }
    }

    /**
     * <p>The JSON form of this query.</p>
     *
     * @return the JSON object
     */
    public Map<String, Object> toJson() {
        val json = new LinkedHashMap<String, Object>();
        json.put(ID, id);
        json.put(FORMAT, format.getValue());
        if (multiple != null) {
            json.put(MULTIPLE, multiple);
        }
        json.put(META, meta == null ? Map.of() : meta);
        if (trustedAuthorities != null && !trustedAuthorities.isEmpty()) {
            json.put(TRUSTED_AUTHORITIES, trustedAuthorities.stream().map(TrustedAuthority::toJson).toList());
        }
        if (requireCryptographicHolderBinding != null) {
            json.put(REQUIRE_CRYPTOGRAPHIC_HOLDER_BINDING, requireCryptographicHolderBinding);
        }
        if (claims != null && !claims.isEmpty()) {
            json.put(CLAIMS, claims.stream().map(ClaimsQuery::toJson).toList());
        }
        if (claimSets != null && !claimSets.isEmpty()) {
            json.put(CLAIM_SETS, claimSets);
        }
        return json;
    }

    /**
     * <p>Read a query from its JSON form.</p>
     *
     * @param json the JSON object
     * @return the query
     */
    public static CredentialQuery fromJson(final Map<String, Object> json) {
        val formatValue = Json.string(json, FORMAT);
        val format = formatValue == null ? null : CredentialFormat.from(formatValue)
            .orElseThrow(() -> new TechnicalException("unsupported credential format in the DCQL query: " + formatValue));
        val query = new CredentialQuery(Json.string(json, ID), format);
        query.setMultiple(Json.bool(json, MULTIPLE));
        if (json.get(META) != null) {
            query.setMeta(new LinkedHashMap<>(Json.object(json.get(META))));
        }
        Json.list(json, TRUSTED_AUTHORITIES).forEach(item -> query.addTrustedAuthority(TrustedAuthority.fromJson(Json.object(item))));
        query.setRequireCryptographicHolderBinding(Json.bool(json, REQUIRE_CRYPTOGRAPHIC_HOLDER_BINDING));
        Json.list(json, CLAIMS).forEach(item -> query.addClaim(ClaimsQuery.fromJson(Json.object(item))));
        Json.list(json, CLAIM_SETS).forEach(item -> query.getClaimSets().add(Json.strings(item)));
        return query;
    }
}
