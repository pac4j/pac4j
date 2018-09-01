package org.pac4j.oauth.profile.hiorgserver;

import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * HiOrg-Server OAuth configuration.
 *
 * @author Martin Böhmer
 * @since 3.1.1
 */
public class HiOrgServerConfiguration extends OAuth20Configuration {

    public final static String DEFAULT_SCOPE = "basic eigenedaten";

    public HiOrgServerConfiguration() {
        setScope(DEFAULT_SCOPE);
        setTokenAsHeader(true);
    }

}
