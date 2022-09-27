package org.pac4j.saml.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.credentials.authenticator.SAML2Authenticator;

import java.time.ZonedDateTime;
import java.util.List;

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

    public SAML2Profile() {
        //default constructor
    }

    /**
     * Create a profile with possibility to merge attributes with the same name and collection-type values.
     * In SAML2 it's very important to get full collection of roles which are received in separate single-element collections.
     * <p>
     * In order to use it you may initialize the client in the following way: <br>
     * <pre>
     * SAML2Client client = new SAML2Client();
     * SAML2ClientConfiguration config = new SAML2ClientConfiguration();
     * SAML2Authenticator authenticator = new SAML2Authenticator(config.getAttributeAsId());
     * boolean canMergeAttributes = true;
     * authenticator.setProfileDefinition(new CommonProfileDefinition&lt;&gt;(x &rarr; new SAML2Profile(canMergeAttributes)));
     * client.setAuthenticator(authenticator);
     * </pre>
     *
     * @param canMergeAttributes if true - merge attributes with the same name and collection-type values, if false - overwrite them.
     * @since 3.1.0
     */
    public SAML2Profile(final boolean canMergeAttributes) {
        super(canMergeAttributes);
    }

    public ZonedDateTime getNotBefore() {
        return (ZonedDateTime) getAuthenticationAttribute(SAML2Authenticator.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE);
    }

    public void setNotBefore(ZonedDateTime notBefore) {
        addAuthenticationAttribute(SAML2Authenticator.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, notBefore);
    }

    public ZonedDateTime getNotOnOrAfter() {
        return (ZonedDateTime) getAuthenticationAttribute(SAML2Authenticator.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE);
    }

    public void setNotOnOrAfter(ZonedDateTime notOnOrAfter) {
        addAuthenticationAttribute(SAML2Authenticator.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, notOnOrAfter);
    }

    public String getSessionIndex() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SESSION_INDEX);
    }

    public void setSessionIndex(String sessionIndex) {
        addAuthenticationAttribute(SAML2Authenticator.SESSION_INDEX, sessionIndex);
    }

    public String getIssuerEntityID() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.ISSUER_ID);
    }

    public void setIssuerEntityID(String issuerEntityID) {
        addAuthenticationAttribute(SAML2Authenticator.ISSUER_ID, issuerEntityID);
    }

    public List<String> getAuthnContexts() {
        return (List<String>) getAuthenticationAttribute(SAML2Authenticator.AUTHN_CONTEXT);
    }

    public void setAuthnContexts(List<String> authnContexts) {
        addAuthenticationAttribute(SAML2Authenticator.AUTHN_CONTEXT, authnContexts);
    }

    public String getSamlNameIdFormat() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_FORMAT);
    }

    public void setSamlNameIdFormat(String samlNameIdFormat) {
        addAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_FORMAT, samlNameIdFormat);
    }

    public String getSamlNameIdNameQualifier() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_NAME_QUALIFIER);
    }

    public void setSamlNameIdNameQualifier(String samlNameIdNameQualifier) {
        addAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_NAME_QUALIFIER, samlNameIdNameQualifier);
    }

    public String getSamlNameIdSpNameQualifier() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_SP_NAME_QUALIFIER);
    }

    public void setSamlNameIdSpNameQualifier(String samlNameIdSpNameQualifier) {
        addAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_SP_NAME_QUALIFIER, samlNameIdSpNameQualifier);
    }

    public String getSamlNameIdSpProviderId() {
        return (String) getAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_SP_PROVIDED_ID);
    }

    public void setSamlNameIdSpProviderId(String samlNameIdSpProviderId) {
        addAuthenticationAttribute(SAML2Authenticator.SAML_NAME_ID_SP_PROVIDED_ID, samlNameIdSpProviderId);
    }
}
