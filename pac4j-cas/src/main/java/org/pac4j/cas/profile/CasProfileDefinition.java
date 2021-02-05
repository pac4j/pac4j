package org.pac4j.cas.profile;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.core.profile.converter.ChainingConverter;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.Arrays;

/**
 * Profile definition for CAS.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasProfileDefinition extends CommonProfileDefinition<CommonProfile> {

    public CasProfileDefinition() {
        super(parameters -> {
            final AttributePrincipal principal = (AttributePrincipal) parameters[0];
            final CasProxyReceptor proxyReceptor = (CasProxyReceptor) parameters[1];
            final CasProfile casProfile;
            if (proxyReceptor != null) {
                casProfile = new CasProxyProfile();
                ((CasProxyProfile) casProfile).setPrincipal(principal);
            } else {
                casProfile = new CasProfile();
            }
            return casProfile;
        });
    }

    @Override
    protected void configurePrimaryAttributes() {
        primary(EMAIL, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(DISPLAY_NAME, Converters.STRING);
        primary(GENDER, Converters.STRING);
        primary(LOCALE, new ChainingConverter(Arrays.asList(Converters.STRING, Converters.LOCALE)));
        primary(PICTURE_URL, Converters.STRING);
        primary(PROFILE_URL, Converters.STRING);
        primary(LOCATION, Converters.STRING);
        primary(Pac4jConstants.USERNAME, Converters.STRING);
    }
}
