package org.pac4j.oauth.profile.orcid;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;
import org.pac4j.scribe.model.OrcidToken;

/**
 * This class is the Orcid profile definition.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidProfileDefinition extends OAuth20ProfileDefinition<OrcidProfile, OAuth20Configuration> {

    public static final String ORCID = "path";
    public static final String FIRST_NAME = "given-names";
    public static final String FAMILY_NAME = "family-name";
    public static final String URI = "uri";
    public static final String CREATION_METHOD = "creation-method";
    public static final String CLAIMED = "claimed";

    public OrcidProfileDefinition() {
        super(x -> new OrcidProfile());
        primary(ORCID, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(URI, Converters.URL);
        primary(CREATION_METHOD, Converters.STRING);
        primary(CLAIMED, Converters.BOOLEAN);
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        if (accessToken instanceof OrcidToken) {
            return String.format("https://api.orcid.org/v1.1/%s/orcid-profile",
                    ((OrcidToken) accessToken).getOrcid());
        } else {
            throw new OAuthException("Token in getProfileUrl is not an OrcidToken");
        }
    }

    @Override
    public OrcidProfile extractUserProfile(String body) {
        OrcidProfile profile = (OrcidProfile) newProfile();
        if (body == null || body.isEmpty()) {
            raiseProfileExtractionError(body);
        }
        profile.setId(CommonHelper.substringBetween(body, "<path>", "</path>"));
        for(final String attribute : getPrimaryAttributes()) {
            convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute,
                    CommonHelper.substringBetween(body, "<" + attribute + ">", "</" + attribute + ">"));
        }
        return profile;
    }
}
