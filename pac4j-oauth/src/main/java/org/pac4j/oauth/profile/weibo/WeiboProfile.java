package org.pac4j.oauth.profile.weibo;

import java.net.URI;
import java.util.Locale;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for  Sina Weibo (using OAuth protocol version 2) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.WeiboClient}.</p>
 *
 * @author Zhang Zhenli
 * @since 3.1.0
 */
public class WeiboProfile extends OAuth20Profile {

    private static final long serialVersionUID = -7486869356444327783L;

    @Override
    public String getFirstName() {
        return (String) getAttribute(WeiboProfileDefinition.name);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(WeiboProfileDefinition.screen_name);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(WeiboProfileDefinition.screen_name);
    }

    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(WeiboProfileDefinition.lang);
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(WeiboProfileDefinition.avatar_hd);
    }

    @Override
    public URI getProfileUrl() {
        final URI attribute = (URI) getAttribute(WeiboProfileDefinition.profile_url);
        if (attribute.isAbsolute()) {
            return attribute;
        } else {
            return CommonHelper.asURI("http://weibo.com/" + attribute.toString());
        }
    }
}
