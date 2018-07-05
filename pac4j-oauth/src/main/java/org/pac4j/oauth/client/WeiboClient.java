package org.pac4j.oauth.client;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.weibo.WeiboProfile;
import org.pac4j.oauth.profile.weibo.WeiboProfileDefinition;
import org.pac4j.scribe.builder.api.WeiboApi20;

/**
 * <p>This class is the OAuth client to authenticate users in Weibo using OAuth protocol version 2.0.</p>
 * <p>The <i>scope</i> is by default : {@link WeiboScope#email}, but it can also but set to : {@link WeiboScope#all}
 * or {@link WeiboScope#email}.</p>
 * <p>It returns a {@link WeiboProfile}.</p>
 * <p>More information at http://open.weibo.com/wiki/Oauth2/access_token/</p>
 *
 * @author Zhang Zhenli
 * @since 3.1.0
 */
public class WeiboClient extends OAuth20Client<WeiboProfile> {

    /**
     * WeiboScope.
     * <p>
     * <p>More info at: <a href=
     * "http://open.weibo.com/wiki/Scope">Scope</a></p>
     */
    public enum WeiboScope {
        all,                        //     Request all of the following scope permissions
        email,                      //    User's contact mailbox
        direct_messages_write,   //     Private message sending interface
        direct_messages_read,    //     Private message reading interfac
        invitation_write,         //     Invitation to send interface
        friendships_groups_read,//    Friend group read interface group
        friendships_groups_write,//    Friend group write interface group
        statuses_to_me_read,     // Directional microblog reading interface group
        follow_app_official_microblog // Pay attention to the application of official Weibo
    }

    protected WeiboScope scope = WeiboScope.email;

    protected String scopeValue;

    public WeiboClient() {
    }

    public WeiboClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotNull("scope", this.scope);
        if (this.scope == null)
            this.scope = WeiboScope.email;
        this.scopeValue = this.scope.toString();
        configuration.setApi(new WeiboApi20());
        configuration.setScope(scopeValue);
        configuration.setProfileDefinition(new WeiboProfileDefinition());
        configuration.setWithState(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR);
            if ("access_denied".equals(error)) {
                return true;
            }
            return false;
        });
        super.clientInit();
    }

    public WeiboScope getScope() {
        return this.scope;
    }

    public void setScope(final WeiboScope scope) {
        this.scope = scope;
    }
}
