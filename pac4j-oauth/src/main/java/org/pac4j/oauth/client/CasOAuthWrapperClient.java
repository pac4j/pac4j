package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfileDefinition;
import org.pac4j.scribe.builder.api.CasOAuthWrapperApi20;

/**
 * <p>This class is the OAuth client to authenticate users on CAS servers using OAuth wrapper.</p>
 * <p>The url of the OAuth endpoint of the CAS server must be set by using the {@link #setCasOAuthUrl(String)} method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile}.</p>
 * <p>More information at https://wiki.jasig.org/display/CASUM/OAuth+server+support</p>
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperClient extends OAuth20Client<CasOAuthWrapperProfile> {
    
    private String casOAuthUrl;
    
    private boolean springSecurityCompliant = false;

    private boolean implicitFlow = false;

    public CasOAuthWrapperClient() {
    }
    
    public CasOAuthWrapperClient(final String key, final String secret, final String casOAuthUrl) {
        setKey(key);
        setSecret(secret);
        this.casOAuthUrl = casOAuthUrl;
    }
    
    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("casOAuthUrl", this.casOAuthUrl);
        configuration.setApi(new CasOAuthWrapperApi20(this.casOAuthUrl, this.springSecurityCompliant, this.implicitFlow));
        configuration.setProfileDefinition(new CasOAuthWrapperProfileDefinition());
        configuration.setHasGrantType(true);
        setConfiguration(configuration);

        super.internalInit(context);
    }

    public String getCasOAuthUrl() {
        return this.casOAuthUrl;
    }
    
    public void setCasOAuthUrl(final String casOAuthUrl) {
        this.casOAuthUrl = casOAuthUrl;
    }
    
    public boolean isSpringSecurityCompliant() {
        return this.springSecurityCompliant;
    }
    
    public void setSpringSecurityCompliant(final boolean springSecurityCompliant) {
        this.springSecurityCompliant = springSecurityCompliant;
    }

    public boolean isImplicitFlow() {
        return implicitFlow;
    }

    public void setImplicitFlow(final boolean implicitFlow) {
        this.implicitFlow = implicitFlow;
    }
}
