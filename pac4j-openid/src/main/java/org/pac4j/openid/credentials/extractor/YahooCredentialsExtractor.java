package org.pac4j.openid.credentials.extractor;

import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.ParameterList;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid.client.YahooOpenIdClient;
import org.pac4j.openid.credentials.OpenIdCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Credentials extractor for Yahoo.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class YahooCredentialsExtractor implements CredentialsExtractor<OpenIdCredentials> {

    private static final Logger logger = LoggerFactory.getLogger(YahooCredentialsExtractor.class);

    private static final String OPENID_MODE = "openid.mode";

    private static final String CANCEL_MODE = "cancel";

    private YahooOpenIdClient client;

    public YahooCredentialsExtractor(final YahooOpenIdClient client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
    }

    @Override
    public Optional<OpenIdCredentials> extract(final WebContext context) {
        final String mode = context.getRequestParameter(OPENID_MODE).orElse(null);
        // cancelled authentication
        if (CommonHelper.areEquals(mode, CANCEL_MODE)) {
            logger.debug("authentication cancelled");
            return Optional.empty();
        }

        // parameters list returned by the provider
        final ParameterList parameterList = new ParameterList(context.getRequestParameters());

        // retrieve the previously stored discovery information
        final DiscoveryInformation discoveryInformation = (DiscoveryInformation) context
                .getSessionStore().get(context, this.client.getDiscoveryInformationSessionAttributeName());

        // create credentials
        final OpenIdCredentials credentials = new OpenIdCredentials(discoveryInformation, parameterList);
        logger.debug("credentials: {}", credentials);
        return Optional.of(credentials);
    }
}
