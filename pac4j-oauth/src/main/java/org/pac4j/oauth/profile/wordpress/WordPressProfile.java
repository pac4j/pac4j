package org.pac4j.oauth.profile.wordpress;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.net.URI;

/**
 * <p>This class is the user profile for WordPress with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.WordPressClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = 6790248892408246089L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new WordPressAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(WordPressAttributesDefinition.AVATAR_URL);
    }
    
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(WordPressAttributesDefinition.PROFILE_URL);
    }
    
    public Integer getPrimaryBlog() {
        return (Integer) getAttribute(WordPressAttributesDefinition.PRIMARY_BLOG);
    }
    
    public WordPressLinks getLinks() {
        return (WordPressLinks) getAttribute(WordPressAttributesDefinition.LINKS);
    }
}
