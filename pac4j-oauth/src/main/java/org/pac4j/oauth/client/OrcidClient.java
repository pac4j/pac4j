package org.pac4j.oauth.client;

import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.orcid.OrcidProfile;
import org.pac4j.scribe.builder.api.OrcidApi20;
import org.pac4j.scribe.model.OrcidToken;

/**
 * <p>This class is the OAuth client to authenticate users in ORCiD.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.orcid.OrcidProfile}.</p>
 * <p>More information at http://support.orcid.org/knowledgebase/articles/175079-tutorial-retrieve-data-from-an-orcid-record-with</p>
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidClient extends BaseOAuth20Client<OrcidProfile> {

    protected static final String DEFAULT_SCOPE = "/orcid-profile/read-limited";

    protected String scope = DEFAULT_SCOPE;

    public OrcidClient() {
        setTokenAsHeader(true);
    }

    public OrcidClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
        setTokenAsHeader(true);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return new OrcidApi20();
    }

    @Override
    protected String getOAuthScope() {
        return this.scope;
    }

    @Override
    protected  boolean hasOAuthGrantType() {
        return true;
    }

    @Override
    protected boolean hasBeenCancelled(WebContext context) {
        final String error = context.getRequestParameter(OAuthCredentialsException.ERROR);
        final String errorDescription = context.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
        // user has denied permissions
        if ("access_denied".equals(error) && "User denied access".equals(errorDescription)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getProfileUrl(final OAuth2AccessToken accessToken) {
        if (accessToken instanceof OrcidToken) {
            return String.format("https://api.orcid.org/v1.1/%s/orcid-profile",
                    ((OrcidToken) accessToken).getOrcid());
        } else {
            throw new OAuthException("Token in getProfileUrl is not an OrcidToken");
        }
    }

    @Override
    protected OrcidProfile extractUserProfile(String body) throws HttpAction {
        OrcidProfile profile = new OrcidProfile();
        for(final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
            profile.addAttribute(attribute, CommonHelper.substringBetween(body, "<" + attribute + ">", "</" + attribute + ">"));
        }
        return profile;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }
}
