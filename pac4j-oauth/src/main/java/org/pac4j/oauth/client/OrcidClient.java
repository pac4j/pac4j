package org.pac4j.oauth.client;

import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.orcid.OrcidProfile;
import org.pac4j.oauth.profile.orcid.OrcidProfileDefinition;
import org.pac4j.scribe.builder.api.OrcidApi20;

/**
 * <p>This class is the OAuth client to authenticate users in ORCiD.</p>
 * <p>It returns a {@link OrcidProfile}.</p>
 * <p>More information at http://support.orcid.org/knowledgebase/articles/175079-tutorial-retrieve-data-from-an-orcid-record-with</p>
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidClient extends OAuth20Client {

    protected static final String DEFAULT_SCOPE = "/authenticate";

    public OrcidClient() {
        setScope(DEFAULT_SCOPE);
    }

    public OrcidClient(final String key, final String secret) {
        setScope(DEFAULT_SCOPE);
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        configuration.setApi(new OrcidApi20());
        configuration.setProfileDefinition(new OrcidProfileDefinition());
        configuration.setTokenAsHeader(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            final String errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION).orElse(null);
            // user has denied permissions
            if ("access_denied".equals(error) && "User denied access".equals(errorDescription)) {
                return true;
            } else {
                return false;
            }
        });

        super.clientInit();
    }

    public String getScope() {
        return getConfiguration().getScope();
    }

    public void setScope(final String scope) {
        getConfiguration().setScope(scope);
    }
}
