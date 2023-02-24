package org.pac4j.oauth.profile.windowslive;

import java.net.URI;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Windows Live with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.WindowsLiveClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveProfile extends OAuth20Profile {

    private static final long serialVersionUID = 1648212768999086087L;

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return (String) getAttribute(WindowsLiveProfileDefinition.LAST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(WindowsLiveProfileDefinition.NAME);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(WindowsLiveProfileDefinition.LINK);
    }
}
