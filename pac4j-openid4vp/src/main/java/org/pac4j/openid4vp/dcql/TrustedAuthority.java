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

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * A trusted authorities query: which authorities or trust frameworks certifying issuers the verifier accepts.
 *
 * <p>Three types are defined: the authority key identifier of a certificate, {@link #AKI}; an ETSI trusted
 * list, {@link #ETSI_TL}; an OpenID federation entity, {@link #OPENID_FEDERATION}.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#credential_query">
 *     OpenID4VP 1.0, trusted authorities query</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class TrustedAuthority {

    /** JSON member names. */
    public static final String TYPE = "type";
    public static final String VALUES = "values";

    /** The types defined by the specification. */
    public static final String AKI = "aki";
    public static final String ETSI_TL = "etsi_tl";
    public static final String OPENID_FEDERATION = "openid_federation";

    private String type;

    private List<String> values = new ArrayList<>();

    /**
     * <p>Constructor for TrustedAuthority.</p>
     *
     * @param type the type
     * @param values the values
     */
    public TrustedAuthority(final String type, final String... values) {
        this.type = type;
        this.values = new ArrayList<>(Arrays.asList(values));
    }

    /**
     * <p>Check this query against the rules of the specification.</p>
     */
    public void check() {
        assertNotBlank("type of a trusted authority", type);
        assertTrue(values != null && !values.isEmpty(), "the values of a trusted authority cannot be empty: " + type);
    }

    /**
     * <p>The JSON form of this query.</p>
     *
     * @return the JSON object
     */
    public Map<String, Object> toJson() {
        val json = new LinkedHashMap<String, Object>();
        json.put(TYPE, type);
        json.put(VALUES, values);
        return json;
    }

    /**
     * <p>Read a query from its JSON form.</p>
     *
     * @param json the JSON object
     * @return the query
     */
    public static TrustedAuthority fromJson(final Map<String, Object> json) {
        val authority = new TrustedAuthority();
        authority.setType(Json.string(json, TYPE));
        authority.setValues(Json.strings(json.get(VALUES)));
        return authority;
    }
}
