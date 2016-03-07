package org.pac4j.saml.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.saml.client.SAML2Client;

/**
 * <p>This class is the user profile for sites using SAML2 protocol.</p>
 * <p>It is returned by the {@link SAML2Client}.</p>
 * 
 * @author Michael Remond
 * @author Misagh Moayyed
 * @version 1.5.0
 */
public class SAML2Profile extends CommonProfile {

    private static final long serialVersionUID = -7811733390277407623L;
}
