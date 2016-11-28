package org.pac4j.oauth.profile.linkedin2;

import java.net.URI;
import java.util.List;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for LinkedIn with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.LinkedIn2Client}.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.1
 */
@SuppressWarnings("unchecked")
public class LinkedIn2Profile extends OAuth20Profile {
    
    private static final long serialVersionUID = -2652388591255880018L;
    
    public String getOAuth10Id() {
        String url = getSiteStandardProfileRequest();
        return CommonHelper.substringBetween(url, "id=", "&authType=");
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.FIRST_NAME);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.LAST_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.FORMATTED_NAME);
    }
    
    @Override
    public String getLocation() {
        LinkedIn2Location location = (LinkedIn2Location) getAttribute(LinkedIn2ProfileDefinition.LOCATION);
        if (location != null) {
            return location.getName();
        } else {
            return null;
        }
    }
    
    @Override
    public String getEmail() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.EMAIL_ADDRESS);
    }
    
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(LinkedIn2ProfileDefinition.PICTURE_URL);
    }
    
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(LinkedIn2ProfileDefinition.PUBLIC_PROFILE_URL);
    }
    
    public LinkedIn2Location getCompleteLocation() {
        return (LinkedIn2Location) getAttribute(LinkedIn2ProfileDefinition.LOCATION);
    }
    
    public String getMaidenName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.MAIDEN_NAME);
    }

    public String getPhoneticFirstName() { return (String) getAttribute(LinkedIn2ProfileDefinition.PHONETIC_FIRST_NAME); }

    public String getPhoneticLastName() { return (String) getAttribute(LinkedIn2ProfileDefinition.PHONETIC_LAST_NAME); }

    public String getFormattedPhoneticName() { return (String) getAttribute(LinkedIn2ProfileDefinition.FORMATTED_PHONETIC_NAME); }

    public String getHeadline() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.HEADLINE);
    }
    
    public String getIndustry() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.INDUSTRY);
    }

    public String getCurrentShare() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.CURRENT_SHARE);
    }
    
    public Integer getNumConnections() {
        return (Integer) getAttribute(LinkedIn2ProfileDefinition.NUM_CONNECTIONS);
    }

    public Boolean getNumConnectionsCapped() {
        return (Boolean) getAttribute(LinkedIn2ProfileDefinition.NUM_CONNECTIONS_CAPPED);
    }
    
    public String getSummary() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.SUMMARY);
    }
    
    public String getSpecialties() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.SPECIALTIES);
    }
    
    public List<LinkedIn2Position> getPositions() {
        return (List<LinkedIn2Position>) getAttribute(LinkedIn2ProfileDefinition.POSITIONS);
    }
    
    public String getSiteStandardProfileRequest() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.SITE_STANDARD_PROFILE_REQUEST);
    }

    public String getApiStandardProfileRequest() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.API_STANDARD_PROFILE_REQUEST);
    }
}
