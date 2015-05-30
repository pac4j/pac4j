package org.pac4j.oauth.profile.ok;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * Created by imayka on 30.05.15.
 */
public class OkProfile extends OAuth20Profile{

    private static final long serialVersionUID = -810631113167677397L;
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.vkDefinition;
    }
}
