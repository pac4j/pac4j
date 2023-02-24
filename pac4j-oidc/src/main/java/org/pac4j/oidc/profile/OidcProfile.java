package org.pac4j.oidc.profile;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.profile.jwt.AbstractJwtProfile;
import org.pac4j.oidc.exceptions.OidcException;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

/**
 * <p>This class is the user profile for sites using OpenID Connect protocol.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.OidcClient}.</p>
 *
 * @author Michael Remond
 * @version 1.7.0
 */
@ToString(callSuper = true)
public class OidcProfile extends AbstractJwtProfile {

    private static final long serialVersionUID = -52855988661742374L;

    /**
     * <p>Constructor for OidcProfile.</p>
     */
    public OidcProfile() { }

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return (String) getAttribute(OidcProfileDefinition.GIVEN_NAME);
    }

    /**
     * <p>getMiddleName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getMiddleName() {
        return (String) getAttribute(OidcProfileDefinition.MIDDLE_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(OidcProfileDefinition.NAME);
    }

    /**
     * <p>getNickname.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getNickname() {
        return (String) getAttribute(OidcProfileDefinition.NICKNAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(OidcProfileDefinition.PREFERRED_USERNAME);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(OidcProfileDefinition.PICTURE);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(OidcProfileDefinition.PROFILE);
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        return (String) getAttribute(OidcProfileDefinition.ZONEINFO);
    }

    /**
     * <p>getEmailVerified.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getEmailVerified() {
        return (Boolean) getAttribute(OidcProfileDefinition.EMAIL_VERIFIED);
    }

    /**
     * <p>getPhoneNumber.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPhoneNumber() {
        return (String) getAttribute(OidcProfileDefinition.PHONE_NUMBER);
    }

    /**
     * <p>getPhoneNumberVerified.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getPhoneNumberVerified() {
        return (Boolean) getAttribute(OidcProfileDefinition.PHONE_NUMBER_VERIFIED);
    }

    /**
     * <p>getUpdatedAt.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getUpdatedAt() {
        return getAttributeAsDate(OidcProfileDefinition.UPDATED_AT);
    }

    /**
     * <p>getAuthTime.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getAuthTime() {
        return (Date) getAttribute(OidcProfileDefinition.AUTH_TIME);
    }

    /**
     * <p>getNonce.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getNonce() {
        return (String) getAttribute(OidcProfileDefinition.NONCE);
    }

    /**
     * <p>getAcr.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAcr() {
        return (String) getAttribute(OidcProfileDefinition.ACR);
    }

    /**
     * <p>getAmr.</p>
     *
     * @return a {@link java.lang.Object} object
     */
    public Object getAmr() {
        return getAttribute(OidcProfileDefinition.AMR);
    }

    /**
     * <p>getAzp.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAzp() {
        return (String) getAttribute(OidcProfileDefinition.AZP);
    }

    /**
     * <p>setAccessToken.</p>
     *
     * @param accessToken a {@link com.nimbusds.oauth2.sdk.token.AccessToken} object
     */
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

    /**
     * <p>getAccessToken.</p>
     *
     * @return a {@link com.nimbusds.oauth2.sdk.token.AccessToken} object
     */
    public AccessToken getAccessToken() {
        return (AccessToken) getAttribute(OidcProfileDefinition.ACCESS_TOKEN);
    }

    /**
     * <p>getIdTokenString.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getIdTokenString() {
        return (String) getAttribute(OidcProfileDefinition.ID_TOKEN);
    }

    /**
     * <p>setIdTokenString.</p>
     *
     * @param idToken a {@link java.lang.String} object
     */
    public void setIdTokenString(final String idToken) {
        addAttribute(OidcProfileDefinition.ID_TOKEN, idToken);
    }

    /**
     * <p>getIdToken.</p>
     *
     * @return a {@link com.nimbusds.jwt.JWT} object
     */
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

    /**
     * <p>getRefreshToken.</p>
     *
     * @return a {@link com.nimbusds.oauth2.sdk.token.RefreshToken} object
     */
    public RefreshToken getRefreshToken() {
        return (RefreshToken) getAttribute(OidcProfileDefinition.REFRESH_TOKEN);
    }

    /**
     * <p>setRefreshToken.</p>
     *
     * @param refreshToken a {@link com.nimbusds.oauth2.sdk.token.RefreshToken} object
     */
    public void setRefreshToken(final RefreshToken refreshToken) {
        addAttribute(OidcProfileDefinition.REFRESH_TOKEN, refreshToken);
    }

    /** {@inheritDoc} */
    @Override
    public void removeLoginData() {
        removeAttribute(OidcProfileDefinition.ID_TOKEN);
        removeAttribute(OidcProfileDefinition.ACCESS_TOKEN);
        removeAttribute(OidcProfileDefinition.REFRESH_TOKEN);
    }

    /**
     * <p>getTokenExpirationAdvance.</p>
     *
     * @return a int
     */
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

    /**
     * <p>setTokenExpirationAdvance.</p>
     *
     * @param tokenExpirationAdvance a int
     */
    public void setTokenExpirationAdvance(int tokenExpirationAdvance) {
        addAttribute(OidcProfileDefinition.TOKEN_EXPIRATION_ADVANCE, tokenExpirationAdvance);
    }

    /**
     * <p>getExpiration.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getExpiration() {
        return getAttributeAsDate(OidcProfileDefinition.EXPIRATION);
    }

    /**
     * <p>setExpiration.</p>
     *
     * @param expiration a {@link java.util.Date} object
     */
    public void setExpiration(final Date expiration) {
        if (expiration != null) {
            addAttribute(OidcProfileDefinition.EXPIRATION, expiration.getTime());
        } else {
            removeAttribute(OidcProfileDefinition.EXPIRATION);
        }
    }

    /** {@inheritDoc} */
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
