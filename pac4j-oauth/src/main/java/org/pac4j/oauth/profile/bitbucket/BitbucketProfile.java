package org.pac4j.oauth.profile.bitbucket;

import org.pac4j.oauth.profile.OAuth10Profile;

import java.net.URI;

/**
 * <p>This class is the user profile for Bitbucket with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.BitbucketClient}.</p>
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketProfile extends OAuth10Profile {

    private static final long serialVersionUID = -8943779913358140436L;

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return (String) getAttribute(BitbucketProfileDefinition.LAST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(BitbucketProfileDefinition.AVATAR);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(BitbucketProfileDefinition.RESOURCE_URI);
    }

    /**
     * <p>isTeam.</p>
     *
     * @return a boolean
     */
    public boolean isTeam() {
        return (Boolean) getAttribute(BitbucketProfileDefinition.IS_TEAM);
    }
}
