/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.pac4j.saml.sso.impl;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.sso.SAML2MessageReceiver;
import org.pac4j.saml.sso.SAML2MessageSender;
import org.pac4j.saml.sso.SAML2ProfileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler capable of sending and receiving SAML messages according to the SAML2 SSO Browser profile.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2WebSSOProfileHandler implements SAML2ProfileHandler<AuthnRequest> {

    private final static Logger logger = LoggerFactory.getLogger(SAML2WebSSOProfileHandler.class);

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

    public Credentials receive(final SAML2MessageContext context) {
        return this.messageReceiver.receiveMessage(context);
    }

}
