package org.pac4j.oauth.profile.bitbucket;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * <p>This class is the user profile for Bitbucket with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.BitbucketClient}.</p>
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = -8943779913358140436L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new BitbucketAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(BitbucketAttributesDefinition.LAST_NAME);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(BitbucketAttributesDefinition.AVATAR);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(BitbucketAttributesDefinition.RESOURCE_URI);
    }
    
    public boolean isTeam() {
        return (Boolean) getAttribute(BitbucketAttributesDefinition.IS_TEAM);
    }
}
