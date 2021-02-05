package org.pac4j.cas.profile;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

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
}
