package org.pac4j.oauth.profile.qq;

import lombok.val;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.io.Serial;
import java.net.URI;

/**
 * <p>This class is the user profile for Tencent QQ Connect with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.QQClient}.</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class QQProfile extends OAuth20Profile {

    @Serial
    private static final long serialVersionUID = -9147667878709777823L;

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(QQProfileDefinition.NICKNAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(QQProfileDefinition.NICKNAME);
    }

    /** {@inheritDoc} */
    @Override
    public Gender getGender() {
        return (Gender) getAttribute(QQProfileDefinition.GENDER);
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        val location = getAttribute(QQProfileDefinition.PROVINCE) + " " + getAttribute(QQProfileDefinition.CITY);
        return location;
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(QQProfileDefinition.FIGUREURL_QQ_2);
    }
}
