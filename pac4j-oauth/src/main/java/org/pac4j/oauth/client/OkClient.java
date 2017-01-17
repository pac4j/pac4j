package org.pac4j.oauth.client;

import com.github.scribejava.apis.OdnoklassnikiApi;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.ok.OkProfileDefinition;
import org.pac4j.oauth.profile.ok.OkProfile;

/**
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public final class OkClient extends OAuth20Client<OkProfile> {

    /**
     * Public key (required as well as application key by API on ok.ru)
     */
    private String publicKey;

    public OkClient() {
    }

    public OkClient(final String key, final String secret,final String publicKey) {
        setKey(key);
        setSecret(secret);
        setPublicKey(publicKey);
    }


    @Override
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotBlank("publicKey", this.publicKey);
        configuration.setApi(OdnoklassnikiApi.instance());
        configuration.setProfileDefinition(new OkProfileDefinition());
        configuration.setHasGrantType(true);
        setConfiguration(configuration);

        super.clientInit(context);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }
}
