package org.pac4j.oidc.profile;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Profile of the JWT ID token.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface JwtIdTokenProfile {

    default String getSubject() {
        return getId();
    }

    default String getIssuer() {
        return (String) getAttribute(OidcIdTokenAttributesDefinition.ISSUER);
    }

    @SuppressWarnings("unchecked")
    default List<String> getAudience() {
        final Object audience = getAttribute(OidcIdTokenAttributesDefinition.AUDIENCE);
        if (audience instanceof String) {
            return Collections.singletonList((String) audience);
        } else {
            return (List<String>) audience;
        }
    }

    default Date getExpirationDate() {
        return (Date) getAttribute(OidcIdTokenAttributesDefinition.EXPIRATION_TIME);
    }

    default Date getIssuedAt() {
        return (Date) getAttribute(OidcIdTokenAttributesDefinition.ISSUED_AT);
    }

    default Date getNbf() {
        return (Date) getAttribute(OidcIdTokenAttributesDefinition.NBF);
    }

    default Date getAuthTime() {
        return (Date) getAttribute(OidcIdTokenAttributesDefinition.AUTH_TIME);
    }

    default String getNonce() {
        return (String) getAttribute(OidcIdTokenAttributesDefinition.NONCE);
    }

    default String getAcr() {
        return (String) getAttribute(OidcIdTokenAttributesDefinition.ACR);
    }

    default Object getAmr() {
        return getAttribute(OidcIdTokenAttributesDefinition.AMR);
    }

    default String getAzp() {
        return (String) getAttribute(OidcIdTokenAttributesDefinition.AZP);
    }

    String getId();

    Object getAttribute(String name);
}
