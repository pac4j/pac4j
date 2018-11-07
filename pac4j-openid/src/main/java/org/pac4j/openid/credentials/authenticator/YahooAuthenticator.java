package org.pac4j.openid.credentials.authenticator;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid.client.YahooOpenIdClient;
import org.pac4j.openid.credentials.OpenIdCredentials;
import org.pac4j.openid.profile.yahoo.YahooOpenIdProfile;
import org.pac4j.openid.profile.yahoo.YahooOpenIdProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for Yahoo.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class YahooAuthenticator implements Authenticator<OpenIdCredentials> {

    private static final Logger logger = LoggerFactory.getLogger(YahooAuthenticator.class);

    private static final ProfileDefinition<YahooOpenIdProfile> PROFILE_DEFINITION = new YahooOpenIdProfileDefinition();

    private YahooOpenIdClient client;

    public YahooAuthenticator(final YahooOpenIdClient client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
    }

    @Override
    public void validate(final OpenIdCredentials credentials, final WebContext context) {
        final ParameterList parameterList = credentials.getParameterList();
        final DiscoveryInformation discoveryInformation = credentials.getDiscoveryInformation();
        logger.debug("parameterList: {}", parameterList);
        logger.debug("discoveryInformation: {}", discoveryInformation);

        try {
            // verify the response
            final VerificationResult verification = this.client.getConsumerManager().verify(this.client.computeFinalCallbackUrl(context),
                    parameterList, discoveryInformation);

            // examine the verification result and extract the verified identifier
            final Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                final AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                logger.debug("authSuccess: {}", authSuccess);

                final YahooOpenIdProfile profile = createProfile(authSuccess);
                profile.setId(verified.getIdentifier());
                logger.debug("profile: {}", profile);
                credentials.setUserProfile(profile);
                return;
            }
        } catch (final OpenIDException e) {
            throw new TechnicalException("OpenID exception", e);
        }

        final String message = "No verifiedId found";
        throw new TechnicalException(message);
    }

    protected YahooOpenIdProfile createProfile(final AuthSuccess authSuccess) throws MessageException {
        final YahooOpenIdProfile profile = PROFILE_DEFINITION.newProfile();

        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
            final FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
            for (final String name : PROFILE_DEFINITION.getPrimaryAttributes()) {
                PROFILE_DEFINITION.convertAndAdd(profile, PROFILE_ATTRIBUTE, name, fetchResp.getAttributeValue(name));
            }
        }
        return profile;
    }
}
