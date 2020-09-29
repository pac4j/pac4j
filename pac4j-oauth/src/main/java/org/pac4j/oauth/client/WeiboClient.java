package org.pac4j.oauth.client;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.weibo.WeiboProfile;
import org.pac4j.oauth.profile.weibo.WeiboProfileDefinition;
import org.pac4j.scribe.builder.api.WeiboApi20;

/**
 * <p>This class is the OAuth client to authenticate users in Weibo using OAuth protocol version 2.0.</p>
 * <p>The <i>scope</i> is by default : {@link WeiboScope#EMAIL}, but it can also but set to : {@link WeiboScope#ALL}
 * or {@link WeiboScope#EMAIL}.</p>
 * <p>It returns a {@link WeiboProfile}.</p>
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

    public WeiboClient() {
    }

    public WeiboClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("scope", this.scope);
        if (this.scope == null)
            this.scope = WeiboScope.EMAIL;
        this.scopeValue = this.scope.toString().toLowerCase();
        configuration.setApi(new WeiboApi20());
        configuration.setScope(scopeValue);
        configuration.setProfileDefinition(new WeiboProfileDefinition());
        configuration.setWithState(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            if ("access_denied".equals(error)) {
                return true;
            }
            return false;
        });

        super.internalInit();
    }

    public WeiboScope getScope() {
        return this.scope;
    }

    public void setScope(final WeiboScope scope) {
        this.scope = scope;
    }
}
