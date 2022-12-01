package org.pac4j.oauth.profile.yahoo;

import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth10Profile;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @Override
    public String getEmail() {
        val emails = getEmails();
        if (emails != null) {
            for (val email : emails) {
                if (email != null && (Boolean.TRUE.equals(email.getPrimary()) || emails.size() == 1)) {
                    return email.getHandle();
                }
            }
        }
        return null;
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(YahooProfileDefinition.GIVEN_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(YahooProfileDefinition.FAMILY_NAME);
    }

    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(YahooProfileDefinition.NICKNAME);
    }

    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(YahooProfileDefinition.LANG);
    }

    @Override
    public URI getPictureUrl() {
        val yahooImage = (YahooImage) getAttribute(YahooProfileDefinition.IMAGE);
        if (yahooImage != null) {
            return CommonHelper.asURI(yahooImage.getImageUrl());
        }
        return null;
    }

    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(YahooProfileDefinition.PROFILE_URL);
    }

    public String getAboutMe() {
        return (String) getAttribute(YahooProfileDefinition.ABOUT_ME);
    }

    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) getAttribute(YahooProfileDefinition.ADDRESSES);
    }

    public Integer getBirthYear() {
        return (Integer) getAttribute(YahooProfileDefinition.BIRTH_YEAR);
    }

    public Date getBirthdate() {
        return (Date) getAttribute(YahooProfileDefinition.BIRTHDATE);
    }

    public Date getCreated() {
        return (Date) getAttribute(YahooProfileDefinition.CREATED);
    }

    public Integer getDisplayAge() {
        return (Integer) getAttribute(YahooProfileDefinition.DISPLAY_AGE);
    }

    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) getAttribute(YahooProfileDefinition.DISCLOSURES);
    }

    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) getAttribute(YahooProfileDefinition.EMAILS);
    }

    public YahooImage getImage() {
        return (YahooImage) getAttribute(YahooProfileDefinition.IMAGE);
    }

    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) getAttribute(YahooProfileDefinition.INTERESTS);
    }

    public Boolean getIsConnected() {
        return (Boolean) getAttribute(YahooProfileDefinition.IS_CONNECTED);
    }

    public Date getMemberSince() {
        return (Date) getAttribute(YahooProfileDefinition.MEMBER_SINCE);
    }

    public String getTimeZone() {
        return (String) getAttribute(YahooProfileDefinition.TIME_ZONE);
    }

    public Date getUpdated() {
        return (Date) getAttribute(YahooProfileDefinition.UPDATED);
    }

    public String getUri() {
        return (String) getAttribute(YahooProfileDefinition.URI);
    }

    public String getAgeCategory() {
        return (String) getAttribute(YahooProfileDefinition.AGE_CATEGORY);
    }
}
