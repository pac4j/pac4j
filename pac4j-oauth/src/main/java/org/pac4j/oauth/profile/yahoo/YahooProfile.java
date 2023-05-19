package org.pac4j.oauth.profile.yahoo;

import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth10Profile;

import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 791758805376191144L;

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return (String) getAttribute(YahooProfileDefinition.GIVEN_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return (String) getAttribute(YahooProfileDefinition.FAMILY_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(YahooProfileDefinition.NICKNAME);
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(YahooProfileDefinition.LANG);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        val yahooImage = (YahooImage) getAttribute(YahooProfileDefinition.IMAGE);
        if (yahooImage != null) {
            return CommonHelper.asURI(yahooImage.getImageUrl());
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(YahooProfileDefinition.PROFILE_URL);
    }

    /**
     * <p>getAboutMe.</p>
     *
     * @return a {@link String} object
     */
    public String getAboutMe() {
        return (String) getAttribute(YahooProfileDefinition.ABOUT_ME);
    }

    /**
     * <p>getAddresses.</p>
     *
     * @return a {@link List} object
     */
    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) getAttribute(YahooProfileDefinition.ADDRESSES);
    }

    /**
     * <p>getBirthYear.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getBirthYear() {
        return (Integer) getAttribute(YahooProfileDefinition.BIRTH_YEAR);
    }

    /**
     * <p>getBirthdate.</p>
     *
     * @return a {@link Date} object
     */
    public Date getBirthdate() {
        return (Date) getAttribute(YahooProfileDefinition.BIRTHDATE);
    }

    /**
     * <p>getCreated.</p>
     *
     * @return a {@link Date} object
     */
    public Date getCreated() {
        return (Date) getAttribute(YahooProfileDefinition.CREATED);
    }

    /**
     * <p>getDisplayAge.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getDisplayAge() {
        return (Integer) getAttribute(YahooProfileDefinition.DISPLAY_AGE);
    }

    /**
     * <p>getDisclosures.</p>
     *
     * @return a {@link List} object
     */
    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) getAttribute(YahooProfileDefinition.DISCLOSURES);
    }

    /**
     * <p>getEmails.</p>
     *
     * @return a {@link List} object
     */
    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) getAttribute(YahooProfileDefinition.EMAILS);
    }

    /**
     * <p>getImage.</p>
     *
     * @return a {@link YahooImage} object
     */
    public YahooImage getImage() {
        return (YahooImage) getAttribute(YahooProfileDefinition.IMAGE);
    }

    /**
     * <p>getInterests.</p>
     *
     * @return a {@link List} object
     */
    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) getAttribute(YahooProfileDefinition.INTERESTS);
    }

    /**
     * <p>getIsConnected.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getIsConnected() {
        return (Boolean) getAttribute(YahooProfileDefinition.IS_CONNECTED);
    }

    /**
     * <p>getMemberSince.</p>
     *
     * @return a {@link Date} object
     */
    public Date getMemberSince() {
        return (Date) getAttribute(YahooProfileDefinition.MEMBER_SINCE);
    }

    /**
     * <p>getTimeZone.</p>
     *
     * @return a {@link String} object
     */
    public String getTimeZone() {
        return (String) getAttribute(YahooProfileDefinition.TIME_ZONE);
    }

    /**
     * <p>getUpdated.</p>
     *
     * @return a {@link Date} object
     */
    public Date getUpdated() {
        return (Date) getAttribute(YahooProfileDefinition.UPDATED);
    }

    /**
     * <p>getUri.</p>
     *
     * @return a {@link String} object
     */
    public String getUri() {
        return (String) getAttribute(YahooProfileDefinition.URI);
    }

    /**
     * <p>getAgeCategory.</p>
     *
     * @return a {@link String} object
     */
    public String getAgeCategory() {
        return (String) getAttribute(YahooProfileDefinition.AGE_CATEGORY);
    }
}
