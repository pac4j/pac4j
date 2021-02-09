package org.pac4j.oauth.profile.qq;

import java.net.URI;

import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.client.QQClient;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Tencent QQ Connect with appropriate getters.</p>
 * <p>It is returned by the {@link QQClient}.</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class QQProfile extends OAuth20Profile {

    private static final long serialVersionUID = -9147667878709777823L;

    @Override
    public String getDisplayName() {
        return (String) getAttribute(QQProfileDefinition.NICKNAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(QQProfileDefinition.NICKNAME);
    }

    @Override
    public Gender getGender() {
        return (Gender) getAttribute(QQProfileDefinition.GENDER);
    }

    @Override
    public String getLocation() {
        final var location = getAttribute(QQProfileDefinition.PROVINCE) + " " + getAttribute(QQProfileDefinition.CITY);
        return location;
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(QQProfileDefinition.FIGUREURL_QQ_2);
    }
}
