package org.pac4j.oauth.profile.dropbox;

import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * <p>This class is the user profile for DropBox with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.DropBoxClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = 6671295443243112368L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new DropBoxAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(DropBoxAttributesDefinition.COUNTRY);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(DropBoxAttributesDefinition.REFERRAL_LINK);
    }
    
    public Long getNormal() {
        return (Long) getAttribute(DropBoxAttributesDefinition.NORMAL);
    }
    
    public Long getQuota() {
        return (Long) getAttribute(DropBoxAttributesDefinition.QUOTA);
    }
    
    public Long getShared() {
        return (Long) getAttribute(DropBoxAttributesDefinition.SHARED);
    }
}
