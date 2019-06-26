package org.pac4j.saml.sso.artifact;

import com.google.common.base.Function;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;

@SuppressWarnings("rawtypes")
public class IssuerFunction implements Function<MessageContext, String> {
    @Override
    public String apply(MessageContext context) {
        if (context == null) {
            return null;
        }
        SAMLObject message = (SAMLObject) context.getMessage();
        Issuer issuer = null;
        if (message instanceof RequestAbstractType) {
            issuer = ((RequestAbstractType) message).getIssuer();
        } else if (message instanceof StatusResponseType) {
            issuer = ((StatusResponseType) message).getIssuer();
        }
        return issuer != null ? issuer.getValue() : null;
    }
}
