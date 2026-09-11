package org.pac4j.openid4vp.dcql;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.pac4j.openid4vp.config.CredentialFormat;

import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.PID_DOCTYPE;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.PID_VCT;

/**
 * The DCQL queries asking for the person identification data (PID) of an EUDI wallet.
 *
 * <p>The attributes are the ones of the PID rulebook, as named in
 * {@link org.pac4j.openid4vp.profile.EudiPidProfileDefinition}. As a SD-JWT VC, a claim is a top-level
 * member of the credential; as a mobile document, it is a data element of the PID namespace, which is
 * the document type itself. Beware that the attribute identifiers changed across versions of the ARF:
 * check them against the version targeted.</p>
 *
 * @see <a href="https://eudi.dev/2.7.3/architecture-and-reference-framework-main/">
 *     EUDI architecture and reference framework, and its PID rulebook</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
public class EudiPidQuery {

    /** The identifier of the PID credential query in the requests built here, and in the responses. */
    public static final String PID = "pid";

    /**
     * <p>A query for the PID as a SD-JWT VC, with the given attributes.</p>
     *
     * @param attributes the attributes asked for
     * @return the query
     */
    public DcqlQuery sdJwtVc(final String... attributes) {
        return new DcqlQuery().addCredential(sdJwtVcCredential(attributes));
    }

    /**
     * <p>A query for the PID as a mobile document, with the given attributes.</p>
     *
     * @param attributes the attributes asked for
     * @return the query
     */
    public DcqlQuery mdoc(final String... attributes) {
        return new DcqlQuery().addCredential(mdocCredential(attributes));
    }

    /**
     * <p>The credential query for the PID as a SD-JWT VC, to be combined with others.</p>
     *
     * @param attributes the attributes asked for
     * @return the credential query
     */
    public CredentialQuery sdJwtVcCredential(final String... attributes) {
        val credential = new CredentialQuery(PID, CredentialFormat.SD_JWT_VC).setVctValues(PID_VCT);
        for (val attribute : attributes) {
            credential.addClaim(attribute);
        }
        return credential;
    }

    /**
     * <p>The credential query for the PID as a mobile document, to be combined with others.</p>
     *
     * @param attributes the attributes asked for
     * @return the credential query
     */
    public CredentialQuery mdocCredential(final String... attributes) {
        val credential = new CredentialQuery(PID, CredentialFormat.MSO_MDOC).setDoctypeValue(PID_DOCTYPE);
        for (val attribute : attributes) {
            // the PID namespace is its document type
            credential.addClaim(PID_DOCTYPE, attribute);
        }
        return credential;
    }
}
