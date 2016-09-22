package org.pac4j.saml.profile;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.saml.client.SAML2Client;

/**
 * <p>This class is the user profile for sites using SAML2 protocol.</p>
 * <p>It is returned by the {@link SAML2Client}.</p>
 * 
 * @author Michael Remond
 * @author Misagh Moayyed
 * @author Ruochao Zheng
 * @version 1.5.0
 */
public class SAML2Profile extends CommonProfile {

    private static final long serialVersionUID = -7811733390277407623L;
    
    public DateTime getNotBefore() {
        return (DateTime) getAttribute(SAML2Client.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE);
    }
    
    public DateTime getNotOnOrAfter() {
        return (DateTime) getAttribute(SAML2Client.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE);
    }

    @Override
    public String getEmail() {
        return getSingleAttributeAsString("email");
    }

    @Override
    public String getFirstName() {
        return getSingleAttributeAsString("first_name");
    }

    @Override
    public String getFamilyName() {
        return getSingleAttributeAsString("family_name");
    }

    @Override
    public String getDisplayName() {
        return getSingleAttributeAsString("display_name");
    }

    @Override
    public String getUsername() {
        return getSingleAttributeAsString(Pac4jConstants.USERNAME);
    }

    @Override
    public Gender getGender() {
        final String key = "gender";
        Object value = getSingleAttribute(key);
        final AttributesDefinition definition = getAttributesDefinition();
        if (definition == null) {
            return Gender.UNSPECIFIED;
        } else {
            Object newValue = definition.convert(key, value);
            if (newValue != null && newValue instanceof Gender) {
                return (Gender) newValue;
            } else {
                return Gender.UNSPECIFIED;
            }
        }
    }

    @Override
    public Locale getLocale() {
        final String key = "locale";
        Object value = getSingleAttribute(key);
        final AttributesDefinition definition = getAttributesDefinition();
        if (definition == null) {
            return null;
        } else {
            Object newValue = definition.convert(key, value);
            if (newValue != null && newValue instanceof Locale) {
                return (Locale) newValue;
            } else {
                return null;
            }
        }
    }

    @Override
    public String getPictureUrl() {
        return getSingleAttributeAsString("picture_url");
    }

    @Override
    public String getProfileUrl() {
        return getSingleAttributeAsString("profile_url");
    }

    @Override
    public String getLocation() {
        return getSingleAttributeAsString("location");
    }

    private Object getSingleAttribute(String name) {
        Object value = getAttribute(name);
        if (value == null) {
            return null;
        } else if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            return list.size() > 0 ? list.get(0).toString() : null;
        } else {
            return value;
        }
    }

    private String getSingleAttributeAsString(String name) {
        Object value = getSingleAttribute(name);
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

}
