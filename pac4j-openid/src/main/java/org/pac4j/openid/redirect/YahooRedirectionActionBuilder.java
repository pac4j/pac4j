package org.pac4j.openid.redirect;

import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.FetchRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid.client.YahooOpenIdClient;
import org.pac4j.openid.profile.yahoo.YahooOpenIdProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Redirection action builder for Yahoo.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class YahooRedirectionActionBuilder implements RedirectionActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(YahooRedirectionActionBuilder.class);

    private static final String YAHOO_GENERIC_USER_IDENTIFIER = "https://me.yahoo.com";

    private YahooOpenIdClient client;

    public YahooRedirectionActionBuilder(final YahooOpenIdClient client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
    }

    @Override
    public RedirectionAction redirect(final WebContext context) {
        try {
            // perform discovery on the user-supplied identifier
            final List discoveries = this.client.getConsumerManager().discover(YAHOO_GENERIC_USER_IDENTIFIER);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            final DiscoveryInformation discoveryInformation = this.client.getConsumerManager().associate(discoveries);

            // save discovery information in session
            context.getSessionStore().set(context, this.client.getDiscoveryInformationSessionAttributeName(), discoveryInformation);

            // create authentication request to be sent to the OpenID provider
            final AuthRequest authRequest = this.client.getConsumerManager().authenticate(discoveryInformation,
                    this.client.computeFinalCallbackUrl(context));

            // create fetch request for attributes
            final FetchRequest fetchRequest = getFetchRequest();
            if (fetchRequest != null) {
                authRequest.addExtension(fetchRequest);
            }

            final String redirectionUrl = authRequest.getDestinationUrl(true);
            logger.debug("redirectionUrl: {}", redirectionUrl);
            return new FoundAction(redirectionUrl);
        } catch (final OpenIDException e) {
            throw new TechnicalException("OpenID exception", e);
        }
    }

    protected FetchRequest getFetchRequest() throws MessageException {
        final FetchRequest fetchRequest = FetchRequest.createFetchRequest();
        fetchRequest.addAttribute(CommonProfileDefinition.EMAIL,
                "http://axschema.org/contact/email", true);
        fetchRequest.addAttribute(YahooOpenIdProfileDefinition.FULLNAME,
                "http://axschema.org/namePerson", true);
        fetchRequest.addAttribute(YahooOpenIdProfileDefinition.LANGUAGE,
                "http://axschema.org/pref/language", true);
        fetchRequest.addAttribute(YahooOpenIdProfileDefinition.IMAGE,
                "http://axschema.org/media/image/default", true);

        logger.debug("fetchRequest: {}", fetchRequest);
        return fetchRequest;
    }
}
