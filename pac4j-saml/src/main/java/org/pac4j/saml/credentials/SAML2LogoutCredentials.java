package org.pac4j.saml.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.credentials.LogoutCredentials;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.saml.context.SAML2MessageContext;

import java.io.Serial;

/**
 * The SAML2 logout credentials.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@EqualsAndHashCode
@ToString
public class SAML2LogoutCredentials extends LogoutCredentials {

    @Serial
    private static final long serialVersionUID = -8601858531025062265L;

    @Getter
    private final SAML2MessageContext context;

    public SAML2LogoutCredentials(final LogoutType type, final SAML2MessageContext context) {
        this.type = type;
        this.context = context;
    }
}
