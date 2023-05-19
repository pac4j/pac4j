package org.pac4j.oauth.profile.vk;

import lombok.val;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.io.Serial;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * <p>This class is the user profile for Vk with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.VkClient}.</p>
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkProfile extends OAuth20Profile {

    @Serial
    private static final long serialVersionUID = -7889265305949082980L;

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return String.format("%s %s", getFirstName(), getLastName()).trim();
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        var domain = getDomain();
        if (domain != null && !domain.isEmpty())
            return domain;
        return getId();
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return CommonHelper.asURI(getFotoMax());
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return CommonHelper.asURI(String.format("https://vk.com/id%s", getId()));
    }

    /** {@inheritDoc} */
    @Override
    public String getEmail() {
        return Pac4jConstants.EMPTY_STRING;
    }

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return getLastName();
    }

    /** {@inheritDoc} */
    @Override
    public Gender getGender() {
        val gender = (Gender) getAttribute(VkProfileDefinition.SEX);
        return Objects.requireNonNullElse(gender, Gender.UNSPECIFIED);
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return (String) getAttribute(VkProfileDefinition.FIRST_NAME);
    }

    /**
     * <p>getLastName.</p>
     *
     * @return a {@link String} object
     */
    public String getLastName() {
        return (String) getAttribute(VkProfileDefinition.LAST_NAME);
    }

    /**
     * <p>getBirthDate.</p>
     *
     * @return a {@link Date} object
     */
    public Date getBirthDate() {
        return (Date) getAttribute(VkProfileDefinition.BIRTH_DATE);
    }

    /**
     * <p>getFoto50.</p>
     *
     * @return a {@link String} object
     */
    public String getFoto50() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_50);
    }

    /**
     * <p>getFoto100.</p>
     *
     * @return a {@link String} object
     */
    public String getFoto100() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_100);
    }

    /**
     * <p>getFoto200Orig.</p>
     *
     * @return a {@link String} object
     */
    public String getFoto200Orig() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_200_ORIG);
    }

    /**
     * <p>getFoto200.</p>
     *
     * @return a {@link String} object
     */
    public String getFoto200() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_200);
    }

    /**
     * <p>getFoto400Orig.</p>
     *
     * @return a {@link String} object
     */
    public String getFoto400Orig() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_400_ORIG);
    }

    /**
     * <p>getFotoMax.</p>
     *
     * @return a {@link String} object
     */
    public String getFotoMax() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_MAX);
    }

    /**
     * <p>getFotoMaxOrig.</p>
     *
     * @return a {@link String} object
     */
    public String getFotoMaxOrig() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_MAX_ORIG);
    }

    /**
     * <p>isOnline.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isOnline() {
        return (Boolean) getAttribute(VkProfileDefinition.ONLINE);
    }

    /**
     * <p>isOnlineMobile.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isOnlineMobile() {
        return (Boolean) getAttribute(VkProfileDefinition.ONLINE_MOBILE);
    }

    /**
     * <p>getDomain.</p>
     *
     * @return a {@link String} object
     */
    public String getDomain() {
        return (String) getAttribute(VkProfileDefinition.DOMAIN);
    }

    /**
     * <p>isHasMobile.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isHasMobile() {
        return (Boolean) getAttribute(VkProfileDefinition.HAS_MOBILE);
    }

    /**
     * <p>getMobilePhone.</p>
     *
     * @return a {@link String} object
     */
    public String getMobilePhone() {
        return (String) getAttribute(VkProfileDefinition.MOBILE_PHONE);
    }

    /**
     * <p>getHomePhone.</p>
     *
     * @return a {@link String} object
     */
    public String getHomePhone() {
        return (String) getAttribute(VkProfileDefinition.HOME_PHONE);
    }

    /**
     * <p>getSkype.</p>
     *
     * @return a {@link String} object
     */
    public String getSkype() {
        return (String) getAttribute(VkProfileDefinition.SKYPE);
    }

    /**
     * <p>getSite.</p>
     *
     * @return a {@link String} object
     */
    public String getSite() {
        return (String) getAttribute(VkProfileDefinition.SITE);
    }

    /**
     * <p>isCanPost.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isCanPost() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_POST);
    }

    /**
     * <p>isCanSeeAllPost.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isCanSeeAllPost() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_SEE_ALL_POST);
    }

    /**
     * <p>isCanSeeAudio.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isCanSeeAudio() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_SEE_AUDIO);
    }

    /**
     * <p>isCanWritePrivateMessage.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isCanWritePrivateMessage() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_WRITE_PRIVATE_MESSAGE);
    }

    /**
     * <p>getStatus.</p>
     *
     * @return a {@link String} object
     */
    public String getStatus() {
        return (String) getAttribute(VkProfileDefinition.STATUS);
    }

    /**
     * <p>getCommonCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getCommonCount() {
        return (Integer) getAttribute(VkProfileDefinition.COMMON_COUNT);
    }

    /**
     * <p>getRelation.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getRelation() {
        return (Integer) getAttribute(VkProfileDefinition.RELATION);
    }
}
