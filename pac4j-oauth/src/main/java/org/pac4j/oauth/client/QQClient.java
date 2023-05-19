package org.pac4j.oauth.client;

import org.pac4j.oauth.profile.qq.QQProfileCreator;
import org.pac4j.oauth.profile.qq.QQProfileDefinition;
import org.pac4j.scribe.builder.api.QQApi20;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class is the OAuth client to authenticate users in Tencent QQ Connect.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.qq.QQProfile}.</p>
 * <p>More information at http://wiki.connect.qq.com/%E4%BD%BF%E7%94%A8authorization_code%E8%8E%B7%E5%8F%96access_token</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class QQClient extends OAuth20Client {

    public enum QQScope {
        /**
         * Get the nickname, avatar, and gender of the logged in user
         */
        GET_USER_INFO,
        /**
         * Get basic information about QQ VIP
         */
        GET_VIP_INFO,
        /**
         * Get advanced information about QQ VIP
         */
        GET_VIP_RICH_INFO,
        /**
         * Get user QQZone album list
         */
        LIST_ALBUM,
        /**
         * Upload a photo to the QQZone album
         */
        UPLOAD_PIC,
        /**
         * Create a new personal album in the user's QQZone album
         */
        ADD_ALBUM,
        /**
         * Get a list of photos in the user's QQZone album
         */
        LIST_PHOTO,
        /**
         * Get the delivery address of Tenpay users
         */
        GET_TENPAY_ADDR
    }


    protected List<QQScope> scopes;


    /**
     * <p>Constructor for QQClient.</p>
     */
    public QQClient() {
    }

    /**
     * <p>Constructor for QQClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public QQClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new QQApi20());
        configuration.setScope(getOAuthScope());
        configuration.setProfileDefinition(new QQProfileDefinition());
        configuration.setWithState(true);
        setProfileCreatorIfUndefined(new QQProfileCreator(configuration, this));

        super.internalInit(forceReinit);
    }

    private String getOAuthScope() {
        StringBuilder builder = null;
        if (scopes != null) {
            for (var value : scopes) {
                if (builder == null) {
                    builder = new StringBuilder();
                } else {
                    builder.append(",");
                }
                builder.append(value.toString().toLowerCase());
            }
        } else {
            builder = new StringBuilder();
            builder.append(QQScope.GET_USER_INFO.toString().toLowerCase());
        }
        return builder == null ? null : builder.toString();
    }

    /**
     * <p>Getter for the field <code>scopes</code>.</p>
     *
     * @return a {@link List} object
     */
    public List<QQScope> getScopes() {
        return scopes;
    }

    /**
     * <p>Setter for the field <code>scopes</code>.</p>
     *
     * @param scopes a {@link List} object
     */
    public void setScopes(List<QQScope> scopes) {
        this.scopes = scopes;
    }

    /**
     * <p>addScope.</p>
     *
     * @param scopes a {@link QQClient.QQScope} object
     */
    public void addScope(QQScope scopes) {
        if (this.scopes == null)
            this.scopes = new ArrayList<>();
        this.scopes.add(scopes);
    }
}
