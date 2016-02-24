package org.pac4j.oauth.profile.orcid;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

/**
 * This class defines the attributes of the {@link OrcidProfile}.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidAttributesDefinition extends AttributesDefinition {

    public static final String ORCID = "path";
    public static final String FIRST_NAME = "given-names";
    public static final String FAMILY_NAME = "family-name";
    public static final String URI = "uri";
    public static final String CREATION_METHOD = "creation-method";
    public static final String CLAIMED = "claimed";
    public static final String LOCALE = "locale";

    public OrcidAttributesDefinition() {
        primary(ORCID, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(URI, Converters.STRING);
        primary(CREATION_METHOD, Converters.STRING);
        primary(CLAIMED, Converters.BOOLEAN);
        primary(LOCALE, Converters.LOCALE);
    }
}
