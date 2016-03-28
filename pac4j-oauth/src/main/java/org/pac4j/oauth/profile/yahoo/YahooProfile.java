package org.pac4j.oauth.profile.yahoo;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * <p>This class is the user profile for Yahoo with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.YahooClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class YahooProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = 791758805376191144L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new YahooAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public String getEmail() {
        final List<YahooEmail> emails = getEmails();
        if (emails != null) {
            for (final YahooEmail email : emails) {
                if (email != null && (Boolean.TRUE.equals(email.getPrimary()) || emails.size() == 1)) {
                    return email.getHandle();
                }
            }
        }
        return null;
    }
    
    @Override
    public String getFirstName() {
        return (String) getAttribute(YahooAttributesDefinition.GIVEN_NAME);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(YahooAttributesDefinition.FAMILY_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }
    
    @Override
    public String getUsername() {
        return (String) getAttribute(YahooAttributesDefinition.NICKNAME);
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(YahooAttributesDefinition.LANG);
    }
    
    @Override
    public String getPictureUrl() {
        final YahooImage yahooImage = (YahooImage) getAttribute(YahooAttributesDefinition.IMAGE);
        if (yahooImage != null) {
            return yahooImage.getImageUrl();
        }
        return null;
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(YahooAttributesDefinition.PROFILE_URL);
    }
    
    public String getAboutMe() {
        return (String) getAttribute(YahooAttributesDefinition.ABOUT_ME);
    }
    
    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) getAttribute(YahooAttributesDefinition.ADDRESSES);
    }
    
    public Integer getBirthYear() {
        return (Integer) getAttribute(YahooAttributesDefinition.BIRTH_YEAR);
    }
    
    public Date getBirthdate() {
        return (Date) getAttribute(YahooAttributesDefinition.BIRTHDATE);
    }
    
    public Date getCreated() {
        return (Date) getAttribute(YahooAttributesDefinition.CREATED);
    }
    
    public Integer getDisplayAge() {
        return (Integer) getAttribute(YahooAttributesDefinition.DISPLAY_AGE);
    }
    
    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) getAttribute(YahooAttributesDefinition.DISCLOSURES);
    }
    
    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) getAttribute(YahooAttributesDefinition.EMAILS);
    }
    
    public YahooImage getImage() {
        return (YahooImage) getAttribute(YahooAttributesDefinition.IMAGE);
    }
    
    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) getAttribute(YahooAttributesDefinition.INTERESTS);
    }
    
    public Boolean getIsConnected() {
        return (Boolean) getAttribute(YahooAttributesDefinition.IS_CONNECTED);
    }
    
    public Date getMemberSince() {
        return (Date) getAttribute(YahooAttributesDefinition.MEMBER_SINCE);
    }
    
    public String getTimeZone() {
        return (String) getAttribute(YahooAttributesDefinition.TIME_ZONE);
    }
    
    public Date getUpdated() {
        return (Date) getAttribute(YahooAttributesDefinition.UPDATED);
    }
    
    public String getUri() {
        return (String) getAttribute(YahooAttributesDefinition.URI);
    }

    public String getAgeCategory() { return (String) getAttribute(YahooAttributesDefinition.AGE_CATEGORY);
    }
}
