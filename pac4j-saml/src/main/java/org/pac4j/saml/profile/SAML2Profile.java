package org.pac4j.saml.profile;

import java.util.List;

import org.joda.time.DateTime;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.credentials.authenticator.SAML2Authenticator;

/**
 * <p>This class is the user profile for sites using SAML2 protocol.</p>
 * <p>It is returned by the {@link SAML2Client}.</p>
 * 
 * @author Michael Remond
 * @author Misagh Moayyed
 * @author Ruochao Zheng
 * @version 1.5.0
 */
public class SAML2Profile extends CommonProfile {

    private static final long serialVersionUID = -7811733390277407623L;
    
    public DateTime getNotBefore() {
        return (DateTime) getAuthenticationAttribute(SAML2Authenticator.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE);
    }
    
    public DateTime getNotOnOrAfter() {
        return (DateTime) getAuthenticationAttribute(SAML2Authenticator.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE);
    }

    public String getSessionIndex() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SESSION_INDEX);
    }
    
    public String getIssuerEntityID() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.ISSUER_ID);
    }
    
    public List<String> getAuthnContexts() {
        return (List<String>) getAuthenticationAttribute(SAML2Authenticator.AUTHN_CONTEXT);
    }

    public String getSamlNameIdFormat() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_FORMAT);
    }

    public String getSamlNameIdNameQualifier() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_NAME_QUALIFIER);
    }

    public String getSamlNameIdSpNameQualifier() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_SP_NAME_QUALIFIER);
    }

    public String getSamlNameIdSpProviderId() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_SP_PROVIDED_ID);
    }

}
