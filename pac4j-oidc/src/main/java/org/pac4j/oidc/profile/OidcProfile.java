package org.pac4j.oidc.profile;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.profile.jwt.AbstractJwtProfile;
import org.pac4j.oidc.client.OidcClient;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import org.pac4j.oidc.exceptions.OidcException;

/**
 * <p>This class is the user profile for sites using OpenID Connect protocol.</p>
 * <p>It is returned by the {@link OidcClient}.</p>
 *
 * @author Michael Remond
 * @version 1.7.0
 */
@ToString(callSuper = true)
public class OidcProfile extends AbstractJwtProfile {

    private static final long serialVersionUID = -52855988661742374L;

    public OidcProfile() { }

    @Override
    public String getFirstName() {
        return (String) getAttribute(OidcProfileDefinition.GIVEN_NAME);
    }

    public String getMiddleName() {
        return (String) getAttribute(OidcProfileDefinition.MIDDLE_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(OidcProfileDefinition.NAME);
    }

    public String getNickname() {
        return (String) getAttribute(OidcProfileDefinition.NICKNAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(OidcProfileDefinition.PREFERRED_USERNAME);
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(OidcProfileDefinition.PICTURE);
    }

    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(OidcProfileDefinition.PROFILE);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(OidcProfileDefinition.ZONEINFO);
    }

    public Boolean getEmailVerified() {
        return (Boolean) getAttribute(OidcProfileDefinition.EMAIL_VERIFIED);
    }

    public String getPhoneNumber() {
        return (String) getAttribute(OidcProfileDefinition.PHONE_NUMBER);
    }

    public Boolean getPhoneNumberVerified() {
        return (Boolean) getAttribute(OidcProfileDefinition.PHONE_NUMBER_VERIFIED);
    }

    public Date getUpdatedAt() {
        return getAttributeAsDate(OidcProfileDefinition.UPDATED_AT);
    }

    public Date getAuthTime() {
        return (Date) getAttribute(OidcProfileDefinition.AUTH_TIME);
    }

    public String getNonce() {
        return (String) getAttribute(OidcProfileDefinition.NONCE);
    }

    public String getAcr() {
        return (String) getAttribute(OidcProfileDefinition.ACR);
    }

    public Object getAmr() {
        return getAttribute(OidcProfileDefinition.AMR);
    }

    public String getAzp() {
        return (String) getAttribute(OidcProfileDefinition.AZP);
    }

    public void setAccessToken(final AccessToken accessToken) {
        addAttribute(OidcProfileDefinition.ACCESS_TOKEN, accessToken);
        if (accessToken != null) {
            if (accessToken.getLifetime() != 0) {
                setExpiration(Date.from(Instant.now().plusSeconds(accessToken.getLifetime())));
            } else {
                Date exp = null;
                try {
                    val jwtClaimsSet = JWTParser.parse(accessToken.getValue()).getJWTClaimsSet();
                    if (jwtClaimsSet != null) {
                        exp = jwtClaimsSet.getExpirationTime();
                    }
                } catch (ParseException e) {
                    logger.trace(e.getMessage(), e);
                }
                setExpiration(exp);
            }
        }
    }

    public AccessToken getAccessToken() {
        return (AccessToken) getAttribute(OidcProfileDefinition.ACCESS_TOKEN);
    }

    public String getIdTokenString() {
        return (String) getAttribute(OidcProfileDefinition.ID_TOKEN);
    }

    public void setIdTokenString(final String idToken) {
        addAttribute(OidcProfileDefinition.ID_TOKEN, idToken);
    }

    public JWT getIdToken() {
        if(getIdTokenString() != null){
            try {
                return JWTParser.parse(getIdTokenString());
            } catch (final ParseException e) {
                throw new OidcException(e);
            }
        } else {
            return null;
        }
    }

    public RefreshToken getRefreshToken() {
        return (RefreshToken) getAttribute(OidcProfileDefinition.REFRESH_TOKEN);
    }

    public void setRefreshToken(final RefreshToken refreshToken) {
        addAttribute(OidcProfileDefinition.REFRESH_TOKEN, refreshToken);
    }

    @Override
    public void removeLoginData() {
        removeAttribute(OidcProfileDefinition.ID_TOKEN);
        removeAttribute(OidcProfileDefinition.ACCESS_TOKEN);
        removeAttribute(OidcProfileDefinition.REFRESH_TOKEN);
    }

    public int getTokenExpirationAdvance() {
        var tokenExpirationAdvance = getAttribute(OidcProfileDefinition.TOKEN_EXPIRATION_ADVANCE);
        if (tokenExpirationAdvance != null) {
            if (tokenExpirationAdvance instanceof Long) {
                return ((Long) tokenExpirationAdvance).intValue();
            } else if (tokenExpirationAdvance instanceof Integer) {
                return (int) tokenExpirationAdvance;
            }
        }
        return 0;
    }

    public void setTokenExpirationAdvance(int tokenExpirationAdvance) {
        addAttribute(OidcProfileDefinition.TOKEN_EXPIRATION_ADVANCE, tokenExpirationAdvance);
    }

    public Date getExpiration() {
        return getAttributeAsDate(OidcProfileDefinition.EXPIRATION);
    }

    public void setExpiration(final Date expiration) {
        if (expiration != null) {
            addAttribute(OidcProfileDefinition.EXPIRATION, expiration.getTime());
        } else {
            removeAttribute(OidcProfileDefinition.EXPIRATION);
        }
    }

    @Override
    public boolean isExpired() {
        var tokenExpirationAdvance = getTokenExpirationAdvance();
        if (tokenExpirationAdvance < 0) {
            return false;
        }
        var expiration = getExpiration();
        return expiration != null
                && expiration.toInstant().isBefore(Instant.now().plusSeconds(tokenExpirationAdvance));
    }
}
