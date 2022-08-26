package org.pac4j.oauth.profile.facebook.converter;

import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.profile.facebook.FacebookRelationshipStatus;

/**
 * This class converts a String into a FacebookRelationshipStatus.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookRelationshipStatusConverter implements AttributeConverter {

    @Override
    public FacebookRelationshipStatus convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof String) {
                var s = ((String) attribute).toLowerCase();
                s = s.replaceAll("_", " ");
                s = s.replaceAll("'", Pac4jConstants.EMPTY_STRING);
                if ("single".equals(s)) {
                    return FacebookRelationshipStatus.SINGLE;
                } else if ("in a relationship".equals(s)) {
                    return FacebookRelationshipStatus.IN_A_RELATIONSHIP;
                } else if ("engaged".equals(s)) {
                    return FacebookRelationshipStatus.ENGAGED;
                } else if ("married".equals(s)) {
                    return FacebookRelationshipStatus.MARRIED;
                } else if ("its complicated".equals(s)) {
                    return FacebookRelationshipStatus.ITS_COMPLICATED;
                } else if ("in an open relationship".equals(s)) {
                    return FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP;
                } else if ("widowed".equals(s)) {
                    return FacebookRelationshipStatus.WIDOWED;
                } else if ("separated".equals(s)) {
                    return FacebookRelationshipStatus.SEPARATED;
                } else if ("divorced".equals(s)) {
                    return FacebookRelationshipStatus.DIVORCED;
                } else if ("in a civil union".equals(s)) {
                    return FacebookRelationshipStatus.IN_A_CIVIL_UNION;
                } else if ("in a domestic partnership".equals(s)) {
                    return FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP;
                }
            } else if (attribute instanceof FacebookRelationshipStatus) {
                return (FacebookRelationshipStatus) attribute;
            }
        }
        return null;
    }
}
