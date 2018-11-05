package org.pac4j.saml.sso.impl;

import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.sso.SAML2MessageReceiver;
import org.pac4j.saml.sso.SAML2MessageSender;
import org.pac4j.saml.sso.SAML2ProfileHandler;

/**
 * Handler capable of sending and receiving SAML logout messages
 * 
 * @author Matthieu Taggiasco
 * @since 2.0.0
 */

public class SAML2LogoutProfileHandler implements SAML2ProfileHandler<LogoutRequest> {

    private final SAML2MessageSender<LogoutRequest> messageSender;

    private final SAML2MessageReceiver messageReceiver;

    public SAML2LogoutProfileHandler(final SAML2MessageSender<LogoutRequest> messageSender,
                                     final SAML2MessageReceiver messageReceiver) {
        this.messageSender = messageSender;
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void send(final SAML2MessageContext context, final LogoutRequest msg, final Object data) {
        this.messageSender.sendMessage(context, msg, data);
    }

    @Override
    public Credentials receive(final SAML2MessageContext context) {
        return this.messageReceiver.receiveMessage(context);
    }
}
