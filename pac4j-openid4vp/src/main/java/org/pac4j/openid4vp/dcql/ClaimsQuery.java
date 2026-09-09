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
 * A claims query: one claim the verifier wants from a credential, by its path, with the values it accepts.
 *
 * <p>The path is a claims path pointer: one segment per element, a string for a member, an integer for an
 * array element, null for every element of an array. For a mobile document, the first segment is the
 * namespace and the second the data element.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#claims_query">
 *     OpenID4VP 1.0, claims query</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class ClaimsQuery {

    /** JSON member names. */
    public static final String ID = "id";
    public static final String PATH = "path";
    public static final String VALUES = "values";
    /** The mobile document specific member: whether the verifier intends to retain the claim. */
    public static final String INTENT_TO_RETAIN = "intent_to_retain";

    /** "REQUIRED if claim_sets is present in the Credential Query; OPTIONAL otherwise." */
    private String id;

    private List<Object> path = new ArrayList<>();

    /** "The expected values of the claim", strings, integers or booleans; any value when absent or empty. */
    private List<Object> values = new ArrayList<>();

    private Boolean intentToRetain;

    /**
     * <p>Constructor for ClaimsQuery.</p>
     *
     * @param path the claims path pointer, one segment per element
     */
    public ClaimsQuery(final Object... path) {
        this.path = new ArrayList<>(Arrays.asList(path));
    }

    /**
     * <p>The expected values of the claim. Named apart from the setter of the list, which a varargs overload
     * would shadow.</p>
     *
     * @param values the values
     * @return this query
     */
    public ClaimsQuery withValues(final Object... values) {
        this.values = new ArrayList<>(Arrays.asList(values));
        return this;
    }

    /**
     * <p>Check this query against the rules of the specification.</p>
     *
     * @param idRequired whether the credential query has claim sets, which need claim identifiers
     */
    public void check(final boolean idRequired) {
        assertTrue(path != null && !path.isEmpty(), "a claim query path cannot be empty");
        for (val segment : path) {
            assertTrue(segment == null || segment instanceof String || segment instanceof Integer,
                "a claim query path segment must be a string, an integer or null: " + segment);
        }
        assertTrue(id == null || CredentialQuery.IDENTIFIER.matcher(id).matches(),
            "a claim identifier must be a non-empty string of alphanumeric, underscore or hyphen characters: " + id);
        assertTrue(!idRequired || id != null, "a claim identifier is required when the credential query has claim sets: " + path);
    }

    /**
     * <p>The JSON form of this query.</p>
     *
     * @return the JSON object
     */
    public Map<String, Object> toJson() {
        val json = new LinkedHashMap<String, Object>();
        if (id != null) {
            json.put(ID, id);
        }
        json.put(PATH, path);
        if (values != null && !values.isEmpty()) {
            json.put(VALUES, values);
        }
        if (intentToRetain != null) {
            json.put(INTENT_TO_RETAIN, intentToRetain);
        }
        return json;
    }

    /**
     * <p>Read a query from its JSON form.</p>
     *
     * @param json the JSON object
     * @return the query
     */
    public static ClaimsQuery fromJson(final Map<String, Object> json) {
        val query = new ClaimsQuery();
        query.setId(Json.string(json, ID));
        query.setPath(new ArrayList<>(Json.list(json, PATH).stream().map(Json::pathSegment).toList()));
        query.setValues(new ArrayList<>(Json.list(json, VALUES)));
        query.setIntentToRetain(Json.bool(json, INTENT_TO_RETAIN));
        return query;
    }
}
