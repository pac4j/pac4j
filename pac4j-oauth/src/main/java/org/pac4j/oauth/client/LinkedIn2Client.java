package org.pac4j.oauth.client;

import com.github.scribejava.apis.LinkedInApi20;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileDefinition;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;

/**
 * <p>This class is the OAuth client to authenticate users in LinkedIn (using OAuth 2.0 protocol).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile}.</p>
 * <p>The scope (by default : <code>r_fullprofile</code>) can be specified using the {@link #setScope(String)} method, as well as the returned
 * fields through the {@link #setFields(String)} method.</p>
 * <p>More information at https://developer.linkedin.com/documents/profile-api</p>
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Client extends OAuth20Client<LinkedIn2Profile> {
    
    public final static String DEFAULT_SCOPE = "r_fullprofile";
    
    protected String scope = DEFAULT_SCOPE;
    
    protected String fields = "id,first-name,last-name,maiden-name,formatted-name,phonetic-first-name,phonetic-last-name,formatted-phonetic-name,headline,location,industry,current-share,num-connections,num-connections-capped,summary,specialties,positions,picture-url,site-standard-profile-request,api-standard-profile-request,public-profile-url,email-address";
    
    public LinkedIn2Client() {
    }
    
    public LinkedIn2Client(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotBlank("scope", this.scope);
        CommonHelper.assertNotBlank("fields", this.fields);
        configuration.setApi(LinkedInApi20.instance());
        configuration.setProfileDefinition(new LinkedIn2ProfileDefinition());
        configuration.setScope(this.scope);
        configuration.setWithState(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR);
            final String errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
            // user has denied permissions
            if ("access_denied".equals(error)
                    && ("the+user+denied+your+request".equals(errorDescription) || "the user denied your request"
                    .equals(errorDescription))) {
                return true;
            } else {
                return false;
            }
        });
        setConfiguration(configuration);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect("https://www.linkedin.com/uas/logout"));

        super.clientInit(context);
    }

    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    public String getFields() {
        return this.fields;
    }
    
    public void setFields(final String fields) {
        this.fields = fields;
    }
}
