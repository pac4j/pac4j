package org.pac4j.cas.profile;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

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
}
