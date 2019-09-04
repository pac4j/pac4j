package org.pac4j.oauth.profile.figshare;

import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * This class is the FigShare profile definition
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class FigShareProfileDefinition extends GenericOAuth20ProfileDefinition<FigShareProfile, OAuth20Configuration> {
    public static final String LAST_NAME = "last_name";

    public FigShareProfileDefinition() {
        super(x -> new FigShareProfile());
        primary(LAST_NAME, Converters.STRING);
    }
}
