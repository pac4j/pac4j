package org.pac4j.oauth.profile.windowslive;

import java.net.URI;
import java.util.Date;

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

    @Override
    public String getFamilyName() {
        return (String) getAttribute(WindowsLiveProfileDefinition.LAST_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(WindowsLiveProfileDefinition.NAME);
    }
    
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(WindowsLiveProfileDefinition.LINK);
    }
    
    public Date getUpdatedTime() {
        return (Date) getAttribute(WindowsLiveProfileDefinition.UPDATED_TIME);
    }
}
