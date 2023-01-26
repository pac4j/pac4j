package org.pac4j.saml.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.saml.context.SAML2MessageContext;

import java.io.Serial;

/**
 * The SAML2 authentication credentials.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SAML2Credentials extends Credentials {

    @Serial
    private static final long serialVersionUID = -9127398090736952238L;

    @Getter
    private final SAML2MessageContext context;

    public SAML2Credentials(final SAML2MessageContext context) {
        this.context = context;
    }

    public SAML2Credentials(final LogoutType type, final SAML2MessageContext context) {
        this.logoutType = type;
        this.context = context;
    }
}
