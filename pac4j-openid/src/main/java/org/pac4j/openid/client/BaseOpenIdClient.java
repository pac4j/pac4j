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
package org.pac4j.openid.client;

import java.util.List;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchRequest;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid.credentials.OpenIdCredentials;

/**
 * This class is a base implementation for an OpenID protocol client based on the openid4java library. It should work
 * for all OpenID clients. In subclasses, some methods are to be implemented / customized for specific needs depending
 * on the client.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseOpenIdClient<U extends CommonProfile> extends IndirectClient<OpenIdCredentials, U> {

    private static final String OPENID_MODE = "openid.mode";

    private static final String CANCEL_MODE = "cancel";

    public final static String DISCOVERY_INFORMATION = "discoveryInformation";

    private ConsumerManager consumerManager;

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        this.consumerManager = new ConsumerManager();
    }

    /**
     * Return the user identifier for the web context.
     * 
     * @param context the web context
     * @return the user identifier
     */
    protected abstract String getUser(WebContext context);

    /**
     * Return the name of the attribute storing in session the discovery information.
     * 
     * @return the name of the attribute storing in session the discovery information
     */
    protected String getDiscoveryInformationSessionAttributeName() {
        return getName() + "#" + DISCOVERY_INFORMATION;
    }

    /**
     * Get a fetch request for attributes.
     * 
     * @return a fetch request for attributes
     * @throws MessageException an OpenID exception
     */
    protected abstract FetchRequest getFetchRequest() throws MessageException;

    @Override
    @SuppressWarnings("rawtypes")
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        final String userIdentifier = getUser(context);
        CommonHelper.assertNotBlank("openIdUser", userIdentifier);

        try {
            // perform discovery on the user-supplied identifier
            final List discoveries = this.consumerManager.discover(userIdentifier);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            final DiscoveryInformation discoveryInformation = this.consumerManager.associate(discoveries);

            // save discovery information in session
            context.setSessionAttribute(getDiscoveryInformationSessionAttributeName(), discoveryInformation);

            final String contextualCallbackUrl = getContextualCallbackUrl(context);
            // create authentication request to be sent to the OpenID provider
            final AuthRequest authRequest = this.consumerManager.authenticate(discoveryInformation,
                    contextualCallbackUrl);

            // create fetch request for attributes
            final FetchRequest fetchRequest = getFetchRequest();
            if (fetchRequest != null) {
                authRequest.addExtension(fetchRequest);
            }

            final String redirectionUrl = authRequest.getDestinationUrl(true);
            logger.debug("redirectionUrl : {}", redirectionUrl);
            return RedirectAction.redirect(redirectionUrl);
        } catch (final OpenIDException e) {
            logger.error("OpenID exception", e);
            throw new TechnicalException("OpenID exception", e);
        }
    }

    @Override
    protected boolean isDirectRedirection() {
        return false;
    }

    @Override
    protected OpenIdCredentials retrieveCredentials(final WebContext context) {
        final String mode = context.getRequestParameter(OPENID_MODE);
        // cancelled authentication
        if (CommonHelper.areEquals(mode, CANCEL_MODE)) {
            logger.debug("authentication cancelled");
            return null;
        }

        // parameters list returned by the provider
        final ParameterList parameterList = new ParameterList(context.getRequestParameters());

        // retrieve the previously stored discovery information
        final DiscoveryInformation discoveryInformation = (DiscoveryInformation) context
                .getSessionAttribute(getDiscoveryInformationSessionAttributeName());

        // create credentials
        final OpenIdCredentials credentials = new OpenIdCredentials(discoveryInformation, parameterList, getName());
        logger.debug("credentials : {}", credentials);
        return credentials;
    }

    /**
     * Create the appropriate OpenID profile.
     * 
     * @param authSuccess the authentication success message
     * @return the appropriate OpenID profile
     * @throws MessageException an OpenID exception
     */
    protected abstract U createProfile(AuthSuccess authSuccess) throws MessageException;

    @Override
    protected U retrieveUserProfile(final OpenIdCredentials credentials, final WebContext context) {
        final ParameterList parameterList = credentials.getParameterList();
        final DiscoveryInformation discoveryInformation = credentials.getDiscoveryInformation();
        logger.debug("parameterList : {}", parameterList);
        logger.debug("discoveryInformation : {}", discoveryInformation);

        try {
            final String contextualCallbackUrl = getContextualCallbackUrl(context);
            // verify the response
            final VerificationResult verification = this.consumerManager.verify(contextualCallbackUrl, parameterList,
                    discoveryInformation);

            // examine the verification result and extract the verified identifier
            final Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                final AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                logger.debug("authSuccess : {}", authSuccess);

                final U profile = createProfile(authSuccess);
                profile.setId(verified.getIdentifier());
                logger.debug("profile : {}", profile);
                return profile;
            }
        } catch (final OpenIDException e) {
            logger.error("OpenID exception", e);
            throw new TechnicalException("OpenID exception", e);
        }

        final String message = "No verifiedId found";
        logger.error(message);
        throw new TechnicalException(message);
    }

    @Override
    public ClientType getClientType() {
        return ClientType.OPENID_PROTOCOL;
    }
}
