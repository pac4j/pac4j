package org.pac4j.openid.client;

import org.openid4java.consumer.ConsumerManager;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.openid.credentials.OpenIdCredentials;
import org.pac4j.openid.credentials.authenticator.YahooAuthenticator;
import org.pac4j.openid.credentials.extractor.YahooCredentialsExtractor;
import org.pac4j.openid.redirect.YahooRedirectActionBuilder;

/**
 * <p>This class is the OpenID client to authenticate users with their yahoo account.</p>
 * <p>It returns a {@link org.pac4j.openid.profile.yahoo.YahooOpenIdProfile}.</p>
 *
 * @see org.pac4j.openid.profile.yahoo.YahooOpenIdProfile
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class YahooOpenIdClient extends IndirectClient<OpenIdCredentials> {

    public final static String DISCOVERY_INFORMATION = "discoveryInformation";

    private ConsumerManager consumerManager;

    @Override
    protected void clientInit() {
        this.consumerManager = new ConsumerManager();
        defaultRedirectActionBuilder(new YahooRedirectActionBuilder(this));
        defaultCredentialsExtractor(new YahooCredentialsExtractor(this));
        defaultAuthenticator(new YahooAuthenticator(this));
    }

    /**
     * Return the name of the attribute storing in session the discovery information.
     *
     * @return the name of the attribute storing in session the discovery information
     */
    public String getDiscoveryInformationSessionAttributeName() {
        return getName() + "#" + DISCOVERY_INFORMATION;
    }

    public ConsumerManager getConsumerManager() {
        return consumerManager;
    }
}
