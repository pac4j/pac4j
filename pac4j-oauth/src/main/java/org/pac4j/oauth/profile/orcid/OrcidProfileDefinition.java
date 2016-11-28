package org.pac4j.oauth.profile.orcid;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * This class is the Orcid profile definition.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidProfileDefinition extends CommonProfileDefinition<OrcidProfile> {

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
}
