package org.pac4j.oauth.client;

import com.github.scribejava.apis.OdnoklassnikiApi;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.ok.OkConfiguration;
import org.pac4j.oauth.profile.ok.OkProfileDefinition;
import org.pac4j.oauth.profile.ok.OkProfile;

/**
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public final class OkClient extends OAuth20Client<OkProfile> {

    public OkClient() {
        configuration = new OkConfiguration();
    }

    public OkClient(final String key, final String secret,final String publicKey) {
        configuration = new OkConfiguration();
        setKey(key);
        setSecret(secret);
        setPublicKey(publicKey);
    }

    @Override
    public OkConfiguration getConfiguration() {
        return (OkConfiguration) configuration;
    }

    @Override
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotBlank("publicKey", getConfiguration().getPublicKey());
        configuration.setApi(OdnoklassnikiApi.instance());
        configuration.setProfileDefinition(new OkProfileDefinition());

        super.clientInit(context);
    }

    public String getPublicKey() {
        return getConfiguration().getPublicKey();
    }

    public void setPublicKey(final String publicKey) {
        getConfiguration().setPublicKey(publicKey);
    }
}
