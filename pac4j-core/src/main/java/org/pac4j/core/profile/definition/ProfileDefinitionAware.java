package org.pac4j.core.profile.definition;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;

/**
 * For classes that can set the profile definition.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class ProfileDefinitionAware<P extends CommonProfile> extends InitializableWebObject {

    private ProfileDefinition<P> profileDefinition;

    public ProfileDefinition<P> getProfileDefinition() {
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
        return profileDefinition;
    }

    public void setProfileDefinition(final ProfileDefinition<P> profileDefinition) {
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
        this.profileDefinition = profileDefinition;
    }

    protected void defaultProfileDefinition(final ProfileDefinition<P> profileDefinition) {
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
        if (this.profileDefinition == null) {
            this.profileDefinition = profileDefinition;
        }
    }
}
