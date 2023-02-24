package org.pac4j.oauth.profile.hiorgserver;

import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * HiOrg-Server OAuth configuration.
 *
 * @author Martin Böhmer
 * @since 3.2.0
 */
public class HiOrgServerConfiguration extends OAuth20Configuration {

    /** Constant <code>DEFAULT_SCOPE="basic eigenedaten"</code> */
    public final static String DEFAULT_SCOPE = "basic eigenedaten";

    /**
     * <p>Constructor for HiOrgServerConfiguration.</p>
     */
    public HiOrgServerConfiguration() {
        setScope(DEFAULT_SCOPE);
        setTokenAsHeader(true);
    }

}
