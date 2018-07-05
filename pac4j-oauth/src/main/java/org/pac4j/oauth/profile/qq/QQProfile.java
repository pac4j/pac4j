package org.pac4j.oauth.profile.qq;

import java.net.URI;

import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.client.QQClient;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Tencent QQ Connect with appropriate getters.</p>
 * <p>It is returned by the {@link QQClient}.</p>
 *
 * @author Zhang Zhenli
 * @since 3.1.0
 */
public class QQProfile extends OAuth20Profile {

    private static final long serialVersionUID = -9147667878709777823L;

    @Override
    public String getDisplayName() {
        return (String) getAttribute(QQProfileDefinition.nickname);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(QQProfileDefinition.nickname);
    }

    @Override
    public Gender getGender() {
        return (Gender) getAttribute(QQProfileDefinition.gender);
    }

    @Override
    public String getLocation() {
        final String location = getAttribute(QQProfileDefinition.province) + " " + getAttribute(QQProfileDefinition.city);
        return location;
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(QQProfileDefinition.figureurl_qq_2);
    }
}
