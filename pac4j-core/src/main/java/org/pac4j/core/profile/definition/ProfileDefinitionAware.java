package org.pac4j.core.profile.definition;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

/**
 * For classes that can set the profile definition.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class ProfileDefinitionAware extends InitializableObject {

    private ProfileDefinition profileDefinition;

    /**
     * <p>Getter for the field <code>profileDefinition</code>.</p>
     *
     * @return a {@link ProfileDefinition} object
     */
    public ProfileDefinition getProfileDefinition() {
        return profileDefinition;
    }

    /**
     * <p>Setter for the field <code>profileDefinition</code>.</p>
     *
     * @param profileDefinition a {@link ProfileDefinition} object
     */
    public void setProfileDefinition(final ProfileDefinition profileDefinition) {
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
        this.profileDefinition = profileDefinition;
    }

    /**
     * <p>setProfileDefinitionIfUndefined.</p>
     *
     * @param profileDefinition a {@link ProfileDefinition} object
     */
    protected void setProfileDefinitionIfUndefined(final ProfileDefinition profileDefinition) {
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
        if (this.profileDefinition == null) {
            this.profileDefinition = profileDefinition;
        }
    }
}
