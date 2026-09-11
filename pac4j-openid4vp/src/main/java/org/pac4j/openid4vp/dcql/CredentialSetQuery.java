package org.pac4j.openid4vp.dcql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * A credential set query: the combinations of credentials which satisfy one use case of the verifier.
 *
 * <p>Each option is a list of credential query identifiers; the wallet returns the credentials of one
 * option. A set is required unless said otherwise, and may state its purpose for the End-User.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#credential_set_query">
 *     OpenID4VP 1.0, credential set query</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class CredentialSetQuery {

    /** JSON member names. */
    public static final String OPTIONS = "options";
    public static final String REQUIRED = "required";
    public static final String PURPOSE = "purpose";

    private List<List<String>> options = new ArrayList<>();

    /** "Whether this set of Credentials is required to satisfy the particular use case", true when absent. */
    private Boolean required;

    /** A string or an object describing the purpose, for the End-User. */
    private Object purpose;

    /**
     * <p>Constructor for CredentialSetQuery.</p>
     *
     * @param options the options, each a list of credential query identifiers
     */
    @SafeVarargs
    public CredentialSetQuery(final List<String>... options) {
        this.options = new ArrayList<>(Arrays.asList(options));
    }

    /**
     * <p>Add an option: one set of credentials satisfying the use case.</p>
     *
     * @param credentialIds the credential query identifiers
     * @return this query
     */
    public CredentialSetQuery addOption(final String... credentialIds) {
        options.add(Arrays.asList(credentialIds));
        return this;
    }

    /**
     * <p>Check this query against the rules of the specification.</p>
     */
    public void check() {
        assertTrue(options != null && !options.isEmpty(), "the options of a credential set cannot be empty");
        for (val option : options) {
            assertTrue(option != null && !option.isEmpty(), "an option of a credential set cannot be empty");
        }
        assertTrue(purpose == null || purpose instanceof String || purpose instanceof Map,
            "the purpose of a credential set must be a string or a JSON object");
    }

    /**
     * <p>The JSON form of this query.</p>
     *
     * @return the JSON object
     */
    public Map<String, Object> toJson() {
        val json = new LinkedHashMap<String, Object>();
        json.put(OPTIONS, options);
        if (required != null) {
            json.put(REQUIRED, required);
        }
        if (purpose != null) {
            json.put(PURPOSE, purpose);
        }
        return json;
    }

    /**
     * <p>Read a query from its JSON form.</p>
     *
     * @param json the JSON object
     * @return the query
     */
    public static CredentialSetQuery fromJson(final Map<String, Object> json) {
        val query = new CredentialSetQuery();
        DcqlMembers.list(json, OPTIONS).forEach(option -> query.getOptions().add(DcqlMembers.strings(option)));
        query.setRequired(DcqlMembers.bool(json, REQUIRED));
        query.setPurpose(json.get(PURPOSE));
        return query;
    }
}
