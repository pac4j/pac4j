package org.pac4j.oauth.client;

import com.github.scribejava.apis.OdnoklassnikiApi;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.ok.OkConfiguration;
import org.pac4j.oauth.profile.ok.OkProfileDefinition;

/**
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public final class OkClient extends OAuth20Client {

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
    protected void internalInit() {
        CommonHelper.assertNotBlank("publicKey", getConfiguration().getPublicKey());
        configuration.setApi(OdnoklassnikiApi.instance());
        configuration.setProfileDefinition(new OkProfileDefinition());

        super.internalInit();
    }

    public String getPublicKey() {
        return getConfiguration().getPublicKey();
    }

    public void setPublicKey(final String publicKey) {
        getConfiguration().setPublicKey(publicKey);
    }
}
