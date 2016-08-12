package org.pac4j.oidc.profile;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oidc.client.OidcClient;

import java.text.ParseException;
import java.util.*;

/**
 * <p>This class is the user profile for sites using OpenID Connect protocol.</p>
 * <p>It is returned by the {@link OidcClient}.</p>
 *
 * @author Michael Remond
 * @version 1.7.0
 */
public class OidcProfile extends CommonProfile {

    private static final long serialVersionUID = -52855988661742374L;

    public OidcProfile() { }

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new OidcAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(OidcAttributesDefinition.GIVEN_NAME);
    }

    public String getMiddleName() {
        return (String) getAttribute(OidcAttributesDefinition.MIDDLE_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(OidcAttributesDefinition.NAME);
    }

    public String getNickname() {
        return (String) getAttribute(OidcAttributesDefinition.NICKNAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(OidcAttributesDefinition.PREFERRED_USERNAME);
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(OidcAttributesDefinition.PICTURE);
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(OidcAttributesDefinition.PROFILE);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(OidcAttributesDefinition.ZONEINFO);
    }

    public Boolean getEmailVerified() {
        return (Boolean) getAttribute(OidcAttributesDefinition.EMAIL_VERIFIED);
    }

    public String getPhoneNumber() {
        return (String) getAttribute(OidcAttributesDefinition.PHONE_NUMBER);
    }

    public Boolean getPhoneNumberVerified() {
        return (Boolean) getAttribute(OidcAttributesDefinition.PHONE_NUMBER_VERIFIED);
    }

    public Date getUpatedAt() {
        return (Date) getAttribute(OidcAttributesDefinition.UPDATED_AT);
    }

    public String getIssuer() {
        return (String) getAttribute(OidcAttributesDefinition.ISSUER);
    }

    @SuppressWarnings("unchecked")
    public List<String> getAudience() {
        final Object audience = getAttribute(OidcAttributesDefinition.AUDIENCE);
        if (audience instanceof String) {
            return Collections.singletonList((String) audience);
        } else {
            return (List<String>) audience;
        }
    }

    public Date getExpirationDate() {
        return (Date) getAttribute(OidcAttributesDefinition.EXPIRATION_TIME);
    }

    public Date getIssuedAt() {
        return (Date) getAttribute(OidcAttributesDefinition.ISSUED_AT);
    }

    public Date getNbf() {
        return (Date) getAttribute(OidcAttributesDefinition.NBF);
    }

    public Date getAuthTime() {
        return (Date) getAttribute(OidcAttributesDefinition.AUTH_TIME);
    }

    public String getNonce() {
        return (String) getAttribute(OidcAttributesDefinition.NONCE);
    }

    public String getAcr() {
        return (String) getAttribute(OidcAttributesDefinition.ACR);
    }

    public Object getAmr() {
        return getAttribute(OidcAttributesDefinition.AMR);
    }

    public String getAzp() {
        return (String) getAttribute(OidcAttributesDefinition.AZP);
    }

    public void setAccessToken(final AccessToken accessToken) {
        addAttribute(OidcAttributesDefinition.ACCESS_TOKEN, accessToken);
    }

    public AccessToken getAccessToken() {
        return (AccessToken) getAttribute(OidcAttributesDefinition.ACCESS_TOKEN);
    }

    public String getIdTokenString() {
        return (String) getAttribute(OidcAttributesDefinition.ID_TOKEN);
    }

    public void setIdTokenString(final String idToken) {
        addAttribute(OidcAttributesDefinition.ID_TOKEN, idToken);
    }

    public JWT getIdToken() {
        try {
            return JWTParser.parse(getIdTokenString());
        } catch (final ParseException e) {
            throw new TechnicalException(e);
        }
    }

    public RefreshToken getRefreshToken() {
        return (RefreshToken) getAttribute(OidcAttributesDefinition.REFRESH_TOKEN);
    }

    public void setRefreshToken(final RefreshToken refreshToken) {
        addAttribute(OidcAttributesDefinition.REFRESH_TOKEN, refreshToken);
    }

    @Override
    public void clearSensitiveData() {
        removeAttribute(OidcAttributesDefinition.ACCESS_TOKEN);
        removeAttribute(OidcAttributesDefinition.ID_TOKEN);
        removeAttribute(OidcAttributesDefinition.REFRESH_TOKEN);
    }
}
