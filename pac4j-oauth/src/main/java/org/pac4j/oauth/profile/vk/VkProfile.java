package org.pac4j.oauth.profile.vk;

import lombok.val;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.net.URI;
import java.util.Date;
import java.util.Locale;

/**
 * <p>This class is the user profile for Vk with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.VkClient}.</p>
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkProfile extends OAuth20Profile {

    private static final long serialVersionUID = -7889265305949082980L;

    @Override
    public String getDisplayName() {
        return String.format("%s %s", getFirstName(), getLastName()).trim();
    }

    @Override
    public String getUsername() {
        var domain = getDomain();
        if (domain != null && !domain.isEmpty())
            return domain;
        return getId();
    }

    @Override
    public URI getPictureUrl() {
        return CommonHelper.asURI(getFotoMax());
    }

    @Override
    public URI getProfileUrl() {
        return CommonHelper.asURI(String.format("https://vk.com/id%s", getId()));
    }

    @Override
    public String getEmail() {
        return Pac4jConstants.EMPTY_STRING;
    }

    @Override
    public String getFamilyName() {
        return getLastName();
    }

    @Override
    public Gender getGender() {
        val gender = (Gender) getAttribute(VkProfileDefinition.SEX);
        if (gender == null) {
            return Gender.UNSPECIFIED;
        } else {
            return gender;
        }
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(VkProfileDefinition.FIRST_NAME);
    }

    public String getLastName() {
        return (String) getAttribute(VkProfileDefinition.LAST_NAME);
    }

    public Date getBirthDate() {
        return (Date) getAttribute(VkProfileDefinition.BIRTH_DATE);
    }

    public String getFoto50() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_50);
    }

    public String getFoto100() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_100);
    }

    public String getFoto200Orig() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_200_ORIG);
    }

    public String getFoto200() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_200);
    }

    public String getFoto400Orig() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_400_ORIG);
    }

    public String getFotoMax() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_MAX);
    }

    public String getFotoMaxOrig() {
        return (String) getAttribute(VkProfileDefinition.PHOTO_MAX_ORIG);
    }

    public Boolean isOnline() {
        return (Boolean) getAttribute(VkProfileDefinition.ONLINE);
    }

    public Boolean isOnlineMobile() {
        return (Boolean) getAttribute(VkProfileDefinition.ONLINE_MOBILE);
    }

    public String getDomain() {
        return (String) getAttribute(VkProfileDefinition.DOMAIN);
    }

    public Boolean isHasMobile() {
        return (Boolean) getAttribute(VkProfileDefinition.HAS_MOBILE);
    }

    public String getMobilePhone() {
        return (String) getAttribute(VkProfileDefinition.MOBILE_PHONE);
    }

    public String getHomePhone() {
        return (String) getAttribute(VkProfileDefinition.HOME_PHONE);
    }

    public String getSkype() {
        return (String) getAttribute(VkProfileDefinition.SKYPE);
    }

    public String getSite() {
        return (String) getAttribute(VkProfileDefinition.SITE);
    }

    public Boolean isCanPost() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_POST);
    }

    public Boolean isCanSeeAllPost() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_SEE_ALL_POST);
    }

    public Boolean isCanSeeAudio() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_SEE_AUDIO);
    }

    public Boolean isCanWritePrivateMessage() {
        return (Boolean) getAttribute(VkProfileDefinition.CAN_WRITE_PRIVATE_MESSAGE);
    }

    public String getStatus() {
        return (String) getAttribute(VkProfileDefinition.STATUS);
    }

    public Integer getCommonCount() {
        return (Integer) getAttribute(VkProfileDefinition.COMMON_COUNT);
    }

    public Integer getRelation() {
        return (Integer) getAttribute(VkProfileDefinition.RELATION);
    }
}
