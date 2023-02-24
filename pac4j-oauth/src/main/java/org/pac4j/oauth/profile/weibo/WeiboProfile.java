package org.pac4j.oauth.profile.weibo;

import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.net.URI;
import java.util.Locale;

/**
 * <p>This class is the user profile for  Sina Weibo (using OAuth protocol version 2) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.WeiboClient}.</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WeiboProfile extends OAuth20Profile {

    private static final long serialVersionUID = -7486869356444327783L;

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return (String) getAttribute(WeiboProfileDefinition.NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(WeiboProfileDefinition.SCREEN_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(WeiboProfileDefinition.SCREEN_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(WeiboProfileDefinition.LANG);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(WeiboProfileDefinition.AVATAR_HD);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        val attribute = (URI) getAttribute(WeiboProfileDefinition.PROFILE_URL);
        if (attribute.isAbsolute()) {
            return attribute;
        } else {
            return CommonHelper.asURI("http://weibo.com/" + attribute.toString());
        }
    }
}
