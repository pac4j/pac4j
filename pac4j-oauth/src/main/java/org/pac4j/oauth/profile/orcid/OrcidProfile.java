package org.pac4j.oauth.profile.orcid;

import org.pac4j.oauth.profile.OAuth20Profile;

import java.net.URI;

/**
 * <p>This class is the user profile for ORCiD with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.OrcidClient}.</p>
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */

public class OrcidProfile extends OAuth20Profile {

    private static final long serialVersionUID = 7626472295622786149L;

    public String getOrcid() {
        return (String) getAttribute(OrcidProfileDefinition.ORCID);
    }

    public boolean getClaimed() {
        return (Boolean) getAttribute(OrcidProfileDefinition.CLAIMED);
    }

    public String getCreationMethod() {
        return (String) getAttribute(OrcidProfileDefinition.CREATION_METHOD);
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(OrcidProfileDefinition.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(OrcidProfileDefinition.FAMILY_NAME);
    }

    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(OrcidProfileDefinition.URI);
    }
}
