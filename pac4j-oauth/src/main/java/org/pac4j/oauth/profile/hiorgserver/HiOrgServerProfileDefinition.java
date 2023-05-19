package org.pac4j.oauth.profile.hiorgserver;

import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.HashSet;
import java.util.Set;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the HiOrg-Server profile definition.
 *
 * @author Martin Böhmer
 * @since 3.2.0
 */
public class HiOrgServerProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>USER_ID="user_id"</code> */
    public static final String USER_ID = "user_id";
    /** Constant <code>USERNAME="username"</code> */
    public static final String USERNAME = "username";

    /** Constant <code>NAME="name"</code> */
    public static final String NAME = "name";
    /** Constant <code>FIRST_NAME="vorname"</code> */
    public static final String FIRST_NAME = "vorname";
    /** Constant <code>FULL_NAME="fullname"</code> */
    public static final String FULL_NAME = "fullname";
    /** Constant <code>ROLES="gruppe"</code> */
    public static final String ROLES = "gruppe";
    /** Constant <code>LEADER="leitung"</code> */
    public static final String LEADER = "leitung";
    /** Constant <code>POSITION="funktion"</code> */
    public static final String POSITION = "funktion";
    /** Constant <code>ORGANISATION_ID="orga"</code> */
    public static final String ORGANISATION_ID = "orga";
    /** Constant <code>ORGANISATION_NAME="organisation"</code> */
    public static final String ORGANISATION_NAME = "organisation";

    /** Constant <code>ALTERNATIVE_ID="alt_user_id"</code> */
    public static final String ALTERNATIVE_ID = "alt_user_id";
    /** Constant <code>TYPED_ALTERNATIVE_ID="typed_alt_user_id"</code> */
    public static final String TYPED_ALTERNATIVE_ID = "typed_alt_user_id";

    /** Constant <code>BASE_URL="https://www.hiorg-server.de/api/oauth2/"{trunked}</code> */
    protected static final String BASE_URL = "https://www.hiorg-server.de/api/oauth2/v1/user.php";

    /**
     * <p>Constructor for HiOrgServerProfileDefinition.</p>
     */
    public HiOrgServerProfileDefinition() {
        super(x -> new HiOrgServerProfile());
        primary(USERNAME, Converters.STRING);
        primary(NAME, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FULL_NAME, Converters.STRING);
        primary(ROLES, Converters.INTEGER);
        primary(LEADER, Converters.BOOLEAN);
        primary(POSITION, Converters.STRING);
        primary(ORGANISATION_ID, Converters.STRING);
        primary(ORGANISATION_NAME, Converters.STRING);
        secondary(ALTERNATIVE_ID, Converters.STRING);
        secondary(TYPED_ALTERNATIVE_ID, Converters.STRING);
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return BASE_URL;
    }

    /** {@inheritDoc} */
    @Override
    public HiOrgServerProfile extractUserProfile(String body) {
        val profile = (HiOrgServerProfile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            logger.debug("Extracting user profile from JSON node " + json);
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, USER_ID)));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            // Secondary attributes are generated from primary attributes
            convertAndAdd(profile, PROFILE_ATTRIBUTE, ALTERNATIVE_ID, profile.getAlternativeId());
            convertAndAdd(profile, PROFILE_ATTRIBUTE, TYPED_ALTERNATIVE_ID, profile.getTypedAlternativeId());
        } else {
            raiseProfileExtractionJsonError(body);
        }
        extractRoles(profile);
        return profile;
    }

    /**
     * <p>extractRoles.</p>
     *
     * @param profile a {@link HiOrgServerProfile} object
     */
    protected void extractRoles(HiOrgServerProfile profile) {
        final Integer rolesAsInt = profile.getRolesAsInteger();
        Set<String> roles = new HashSet<>();
        for (var i = 0; i <= 10; i++) {
            var groupId = (int) Math.pow(2, i);
            var isGroupSet = (rolesAsInt & groupId) == groupId;
            if (isGroupSet) {
                logger.debug("Extracted role " + groupId);
                roles.add(String.valueOf(groupId));
            }
        }
        profile.setRoles(roles);
    }

}
