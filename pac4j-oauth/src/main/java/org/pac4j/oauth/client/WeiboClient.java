package org.pac4j.oauth.client;

import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.weibo.WeiboProfileDefinition;
import org.pac4j.scribe.builder.api.WeiboApi20;

/**
 * <p>This class is the OAuth client to authenticate users in Weibo using OAuth protocol version 2.0.</p>
 * <p>The <i>scope</i> is by default : {@link org.pac4j.oauth.client.WeiboClient.WeiboScope#EMAIL},
 * but it can also but set to : {@link org.pac4j.oauth.client.WeiboClient.WeiboScope#ALL} or
 * {@link org.pac4j.oauth.client.WeiboClient.WeiboScope#EMAIL}.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.weibo.WeiboProfile}.</p>
 * <p>More information at http://open.weibo.com/wiki/Oauth2/access_token/</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WeiboClient extends OAuth20Client {

    /**
     * WeiboScope.
     * <p>More info at: <a href=
     * "http://open.weibo.com/wiki/Scope">Scope</a></p>
     */
    public enum WeiboScope {
        ALL,                        //     Request all of the following scope permissions
        EMAIL,                      //    User's contact mailbox
        DIRECT_MESSAGES_WRITE,   //     Private message sending interface
        DIRECT_MESSAGES_READ,    //     Private message reading interfac
        INVITATION_WRITE,         //     Invitation to send interface
        FRIENDSHIPS_GROUPS_READ,//    Friend group read interface group
        FRIENDSHIPS_GROUPS_WRITE,//    Friend group write interface group
        STATUSES_TO_ME_READ,     // Directional microblog reading interface group
        FOLLOW_APP_OFFICIAL_MICROBLOG // Pay attention to the application of official Weibo
    }

    protected WeiboScope scope = WeiboScope.EMAIL;

    protected String scopeValue;

    /**
     * <p>Constructor for WeiboClient.</p>
     */
    public WeiboClient() {
    }

    /**
     * <p>Constructor for WeiboClient.</p>
     *
     * @param key a {@link java.lang.String} object
     * @param secret a {@link java.lang.String} object
     */
    public WeiboClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("scope", this.scope);
        if (this.scope == null)
            this.scope = WeiboScope.EMAIL;
        this.scopeValue = this.scope.toString().toLowerCase();
        configuration.setApi(new WeiboApi20());
        configuration.setScope(scopeValue);
        configuration.setProfileDefinition(new WeiboProfileDefinition());
        configuration.setWithState(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            val error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            if ("access_denied".equals(error)) {
                return true;
            }
            return false;
        });

        super.internalInit(forceReinit);
    }

    /**
     * <p>Getter for the field <code>scope</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.client.WeiboClient.WeiboScope} object
     */
    public WeiboScope getScope() {
        return this.scope;
    }

    /**
     * <p>Setter for the field <code>scope</code>.</p>
     *
     * @param scope a {@link org.pac4j.oauth.client.WeiboClient.WeiboScope} object
     */
    public void setScope(final WeiboScope scope) {
        this.scope = scope;
    }
}
