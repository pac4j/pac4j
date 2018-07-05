package org.pac4j.oauth.client;

import java.util.ArrayList;
import java.util.List;

import org.pac4j.oauth.profile.qq.QQProfile;
import org.pac4j.oauth.profile.qq.QQProfileCreator;
import org.pac4j.oauth.profile.qq.QQProfileDefinition;
import org.pac4j.scribe.builder.api.QQApi20;

/**
 * <p>This class is the OAuth client to authenticate users in Tencent QQ Connect.</p>
 * <p>It returns a {@link QQProfile}.</p>
 * <p>More information at http://wiki.connect.qq.com/%E4%BD%BF%E7%94%A8authorization_code%E8%8E%B7%E5%8F%96access_token</p>
 *
 * @author Zhang Zhenli
 * @since 3.1.0
 */
public class QQClient extends OAuth20Client<QQProfile> {

    public enum TencentQQScope {
        /**
         * Get the nickname, avatar, and gender of the logged in user
         */
        get_user_info,
        /**
         * Get basic information about QQ VIP
         */
        get_vip_info,
        /**
         * Get advanced information about QQ VIP
         */
        get_vip_rich_info,
        /**
         * Get user QQZone album list
         */
        list_album,
        /**
         * Upload a photo to the QQZone album
         */
        upload_pic,
        /**
         * Create a new personal album in the user's QQZone album
         */
        add_album,
        /**
         * Get a list of photos in the user's QQZone album
         */
        list_photo,
        /**
         * Get the delivery address of Tenpay users
         */
        get_tenpay_addr
    }


    protected List<TencentQQScope> scopes;


    public QQClient() {
    }

    public QQClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        configuration.setApi(new QQApi20());
        configuration.setScope(getOAuthScope());
        configuration.setProfileDefinition(new QQProfileDefinition());
        configuration.setWithState(true);
        defaultProfileCreator(new QQProfileCreator(configuration, this));
        super.clientInit();
    }

    private String getOAuthScope() {
        StringBuilder builder = null;
        if (scopes != null) {
            for (TencentQQScope value : scopes) {
                if (builder == null) {
                    builder = new StringBuilder();
                } else {
                    builder.append(",");
                }
                builder.append(value.toString());
            }
        }
        return builder == null ? null : builder.toString();
    }

    public List<TencentQQScope> getScopes() {
        return scopes;
    }

    public void setScopes(List<TencentQQScope> scopes) {
        this.scopes = scopes;
    }

    public void addScope(TencentQQScope scopes) {
        if (this.scopes == null)
            this.scopes = new ArrayList<>();
        this.scopes.add(scopes);
    }
}
