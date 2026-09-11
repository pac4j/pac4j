package org.pac4j.openid4vp.dcql;

import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * A DCQL query: which credentials the verifier asks for, and which combinations of them satisfy it.
 *
 * <p>The query is written, checked and read here; matching it against the credentials is the wallet's
 * job. Built programmatically, or parsed from its JSON form with {@link #parse(String)}, it is sent as the
 * {@code dcql_query} request parameter through {@link #toJson()}.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#dcql_query">
 *     OpenID4VP 1.0, Digital Credentials Query Language</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DcqlQuery {

    /** JSON member names. */
    public static final String CREDENTIALS = "credentials";
    public static final String CREDENTIAL_SETS = "credential_sets";

    /** "A non-empty array of Credential Queries [...] that specify the requested Credentials." */
    private List<CredentialQuery> credentials = new ArrayList<>();

    /** "A non-empty array of Credential Set Queries [...] additional constraints on which of the requested Credentials to return." */
    private List<CredentialSetQuery> credentialSets = new ArrayList<>();

    /**
     * <p>Add a credential query.</p>
     *
     * @param credential the credential query
     * @return this query
     */
    public DcqlQuery addCredential(final CredentialQuery credential) {
        credentials.add(credential);
        return this;
    }

    /**
     * <p>Add a credential set query.</p>
     *
     * @param credentialSet the credential set query
     * @return this query
     */
    public DcqlQuery addCredentialSet(final CredentialSetQuery credentialSet) {
        credentialSets.add(credentialSet);
        return this;
    }

    /**
     * <p>Find a credential query by its identifier, the one indexing the presentations in the response.</p>
     *
     * @param id the identifier
     * @return the credential query, if any
     */
    public Optional<CredentialQuery> findCredential(final String id) {
        return credentials.stream().filter(credential -> credential.getId().equals(id)).findFirst();
    }

    /**
     * <p>Check the query against the rules of the specification: non-empty, unique identifiers, and sets
     * referencing existing credentials.</p>
     */
    public void check() {
        assertTrue(credentials != null && !credentials.isEmpty(), "credentials cannot be empty");
        val ids = new HashSet<String>();
        for (val credential : credentials) {
            credential.check();
            // "Within the Authorization Request, the same id MUST NOT be present more than once"
            assertTrue(ids.add(credential.getId()), "duplicate credential query identifier: " + credential.getId());
        }
        if (credentialSets != null) {
            for (val credentialSet : credentialSets) {
                credentialSet.check();
                for (val option : credentialSet.getOptions()) {
                    for (val id : option) {
                        // "a non-empty array of identifiers which reference elements in credentials"
                        assertTrue(ids.contains(id), "unknown credential query identifier in a credential set: " + id);
                    }
                }
            }
        }
    }

    /**
     * <p>The JSON form of the query.</p>
     *
     * @return the JSON object
     */
    public Map<String, Object> toJson() {
        val json = new LinkedHashMap<String, Object>();
        json.put(CREDENTIALS, credentials.stream().map(CredentialQuery::toJson).toList());
        if (credentialSets != null && !credentialSets.isEmpty()) {
            json.put(CREDENTIAL_SETS, credentialSets.stream().map(CredentialSetQuery::toJson).toList());
        }
        return json;
    }

    /**
     * <p>The JSON form of the query, serialized.</p>
     *
     * @return the JSON string
     */
    public String toJsonString() {
        return JSONObjectUtils.toJSONString(toJson());
    }

    /**
     * <p>Read a query from its JSON form.</p>
     *
     * @param json the JSON string
     * @return the query
     */
    public static DcqlQuery parse(final String json) {
        try {
            return fromJson(JSONObjectUtils.parse(json));
        } catch (final ParseException e) {
            throw new TechnicalException("dcqlQuery is not a JSON object: " + e.getMessage(), e);
        }
    }

    /**
     * <p>Read a query from its JSON form.</p>
     *
     * @param json the JSON object
     * @return the query
     */
    public static DcqlQuery fromJson(final Map<String, Object> json) {
        val query = new DcqlQuery();
        DcqlMembers.list(json, CREDENTIALS).forEach(item -> query.addCredential(CredentialQuery.fromJson(DcqlMembers.object(item))));
        DcqlMembers.list(json, CREDENTIAL_SETS)
            .forEach(item -> query.addCredentialSet(CredentialSetQuery.fromJson(DcqlMembers.object(item))));
        return query;
    }
}
