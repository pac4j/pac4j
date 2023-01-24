package org.pac4j.saml.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.saml.context.SAML2MessageContext;

import java.io.Serial;

/**
 * The SAML2 authentication credentials.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@ToString
public class SAML2AuthenticationCredentials extends AuthenticationCredentials {

    @Serial
    private static final long serialVersionUID = -9127398090736952238L;

    @Getter
    private final SAML2MessageContext context;
}
