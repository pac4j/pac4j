package org.pac4j.saml.sso.impl;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.api.SAML2MessageReceiver;
import org.pac4j.saml.profile.api.SAML2MessageSender;
import org.pac4j.saml.profile.api.SAML2ProfileHandler;

/**
 * Handler capable of sending and receiving SAML messages according to the SAML2 SSO Browser profile.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2WebSSOProfileHandler implements SAML2ProfileHandler<AuthnRequest> {

    private final SAML2MessageSender<AuthnRequest> messageSender;

    private final SAML2MessageReceiver messageReceiver;

    public SAML2WebSSOProfileHandler(final SAML2MessageSender<AuthnRequest> messageSender,
                                     final SAML2MessageReceiver messageReceiver) {
        this.messageSender = messageSender;
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void send(final SAML2MessageContext context, final AuthnRequest msg, final Object data) {
        this.messageSender.sendMessage(context, msg, data);
    }

    @Override
    public Credentials receive(final SAML2MessageContext context) {
        return this.messageReceiver.receiveMessage(context);
    }
}
