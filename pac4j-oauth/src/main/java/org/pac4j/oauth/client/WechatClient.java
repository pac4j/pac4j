package org.pac4j.oauth.client;

import org.pac4j.oauth.profile.wechat.WechatProfileCreator;
import org.pac4j.oauth.profile.wechat.WechatProfileDefinition;
import org.pac4j.scribe.builder.api.WechatApi20;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class is the OAuth client to authenticate users in Tencent Wechat.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.wechat.WechatProfile}.</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatClient extends OAuth20Client {

    public enum WechatScope {
        /**
         * Only for WeChat QRCode login. Get the nickname, avatar, and gender of the logged in user.
         */
        SNSAPI_LOGIN,
        /**
         * Exchange code for access_token, refresh_token, and authorized scope
         */
        SNSAPI_BASE,
        /**
         * Get user personal information
         */
        SNSAPI_USERINFO
    }

    protected List<WechatScope> scopes;


    /**
     * <p>Constructor for WechatClient.</p>
     */
    public WechatClient() {
    }

    /**
     * <p>Constructor for WechatClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public WechatClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new WechatApi20());
        configuration.setScope(getOAuthScope());
        configuration.setProfileDefinition(new WechatProfileDefinition());
        configuration.setWithState(true);
        setProfileCreatorIfUndefined(new WechatProfileCreator(configuration, this));

        super.internalInit(forceReinit);
    }

    /**
     * <p>getOAuthScope.</p>
     *
     * @return a {@link String} object
     */
    protected String getOAuthScope() {
        StringBuilder builder = null;
        if (scopes == null || scopes.isEmpty()) {
            scopes = new ArrayList<>();
            scopes.add(WechatScope.SNSAPI_BASE);
        }
        if (scopes != null) {
            for (var value : scopes) {
                if (builder == null) {
                    builder = new StringBuilder();
                } else {
                    builder.append(",");
                }
                builder.append(value.toString().toLowerCase());
            }
        }
        return builder == null ? null : builder.toString();
    }

    /**
     * <p>Getter for the field <code>scopes</code>.</p>
     *
     * @return a {@link List} object
     */
    public List<WechatScope> getScopes() {
        return scopes;
    }

    /**
     * <p>Setter for the field <code>scopes</code>.</p>
     *
     * @param scopes a {@link List} object
     */
    public void setScopes(List<WechatScope> scopes) {
        this.scopes = scopes;
    }

    /**
     * <p>addScope.</p>
     *
     * @param scopes a {@link WechatClient.WechatScope} object
     */
    public void addScope(WechatScope scopes) {
        if (this.scopes == null)
            this.scopes = new ArrayList<>();
        this.scopes.add(scopes);
    }
}
