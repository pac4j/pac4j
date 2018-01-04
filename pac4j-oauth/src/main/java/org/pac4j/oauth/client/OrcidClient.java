package org.pac4j.oauth.client;

import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.orcid.OrcidProfile;
import org.pac4j.oauth.profile.orcid.OrcidProfileDefinition;
import org.pac4j.scribe.builder.api.OrcidApi20;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in ORCiD.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.orcid.OrcidProfile}.</p>
 * <p>More information at http://support.orcid.org/knowledgebase/articles/175079-tutorial-retrieve-data-from-an-orcid-record-with</p>
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidClient extends OAuth20Client<OrcidProfile> {

    protected static final String DEFAULT_SCOPE = "/orcid-profile/read-limited";

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
            final Optional<String> error = ctx.getRequestParameter(OAuthCredentialsException.ERROR);
            final Optional<String> errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
            // user has denied permissions
            return error.map(e -> "access_denied".equals(e)).orElse(false)
                && errorDescription.map(e -> "User denied access".equals(e)).orElse(false);
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
