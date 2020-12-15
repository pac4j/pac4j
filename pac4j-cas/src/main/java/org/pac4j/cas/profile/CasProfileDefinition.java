package org.pac4j.cas.profile;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.converter.ChainingConverter;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.List;

/**
 * Profile definition for CAS.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasProfileDefinition extends CommonProfileDefinition {

    public CasProfileDefinition() {
        super(parameters -> new CasProfile());
    }

    @Override
    public UserProfile newProfile(final Object... parameters) {
        final CasProxyReceptor proxyReceptor = (CasProxyReceptor) getParameter(parameters, 1);
        if (proxyReceptor != null) {
            final CasProxyProfile profile = new CasProxyProfile();
            profile.setPrincipal((AttributePrincipal) getParameter(parameters, 2));
            return profile;
        } else {
            return super.newProfile(parameters);
        }
    }

    @Override
    protected void configurePrimaryAttributes() {
        primary(EMAIL, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(DISPLAY_NAME, Converters.STRING);
        primary(GENDER, Converters.STRING);
        primary(LOCALE, new ChainingConverter(List.of(Converters.STRING, Converters.LOCALE)));
        primary(PICTURE_URL, Converters.STRING);
        primary(PROFILE_URL, Converters.STRING);
        primary(LOCATION, Converters.STRING);
        primary(Pac4jConstants.USERNAME, Converters.STRING);
    }
}
