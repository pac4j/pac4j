package org.pac4j.oauth.profile.windowslive;

import java.util.Date;

import org.pac4j.core.profile.AttributesDefinition;
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

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new WindowsLiveAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(WindowsLiveAttributesDefinition.LAST_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(WindowsLiveAttributesDefinition.NAME);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(WindowsLiveAttributesDefinition.LINK);
    }
    
    public Date getUpdatedTime() {
        return (Date) getAttribute(WindowsLiveAttributesDefinition.UPDATED_TIME);
    }
}
