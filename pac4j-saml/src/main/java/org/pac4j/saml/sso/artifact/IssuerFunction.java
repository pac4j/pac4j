package org.pac4j.saml.sso.artifact;

import com.google.common.base.Function;
import lombok.val;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;

/**
 * A simple function that returns the issuer set on the {@link MessageContext}.
 * This is read from the message in the context.
 *
 * @since 3.8.0
 */
@SuppressWarnings("rawtypes")
public class IssuerFunction implements Function<MessageContext, String> {
    @Override
    public String apply(final MessageContext context) {
        if (context == null) {
            return null;
        }
        val message = (SAMLObject) context.getMessage();
        Issuer issuer = null;
        if (message instanceof RequestAbstractType) {
            issuer = ((RequestAbstractType) message).getIssuer();
        } else if (message instanceof StatusResponseType) {
            issuer = ((StatusResponseType) message).getIssuer();
        }
        return issuer != null ? issuer.getValue() : null;
    }
}
