package org.pac4j.oidc.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;
import net.minidev.json.JSONObject;
import org.pac4j.core.profile.jwt.AbstractJwtProfile;
import org.pac4j.oidc.exceptions.OidcException;

import java.io.Serial;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * <p>This class is the user profile for sites using OpenID Connect protocol.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.OidcClient}.</p>
 *
 * @author Michael Remond
 * @version 1.7.0
 */
@ToString(callSuper = true)
@NoArgsConstructor
public class OidcProfile extends AbstractJwtProfile {

    @Serial
    private static final long serialVersionUID = -52855988661742374L;

    @Override
    @JsonIgnore
    public String getFirstName() {
        return (String) getAttribute(OidcProfileDefinition.GIVEN_NAME);
    }

    @JsonIgnore
    public String getMiddleName() {
        return (String) getAttribute(OidcProfileDefinition.MIDDLE_NAME);
    }

    @Override
    @JsonIgnore
    public String getDisplayName() {
        return (String) getAttribute(OidcProfileDefinition.NAME);
    }

    @JsonIgnore
    public String getNickname() {
        return (String) getAttribute(OidcProfileDefinition.NICKNAME);
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return (String) getAttribute(OidcProfileDefinition.PREFERRED_USERNAME);
    }

    @Override
    @JsonIgnore
    public URI getPictureUrl() {
        return (URI) getAttribute(OidcProfileDefinition.PICTURE);
    }

    @Override
    @JsonIgnore
    public URI getProfileUrl() {
        return (URI) getAttribute(OidcProfileDefinition.PROFILE);
    }

    @Override
    @JsonIgnore
    public String getLocation() {
        return (String) getAttribute(OidcProfileDefinition.ZONEINFO);
    }

    @JsonIgnore
    public Boolean getEmailVerified() {
        return (Boolean) getAttribute(OidcProfileDefinition.EMAIL_VERIFIED);
    }

    @JsonIgnore
    public String getPhoneNumber() {
        return (String) getAttribute(OidcProfileDefinition.PHONE_NUMBER);
    }

    @JsonIgnore
    public Boolean getPhoneNumberVerified() {
        return (Boolean) getAttribute(OidcProfileDefinition.PHONE_NUMBER_VERIFIED);
    }

    @JsonIgnore
    public Date getUpdatedAt() {
        return getAttributeAsDate(OidcProfileDefinition.UPDATED_AT);
    }

    @JsonIgnore
    public Date getAuthTime() {
        return (Date) getAttribute(OidcProfileDefinition.AUTH_TIME);
    }

    @JsonIgnore
    public String getNonce() {
        return (String) getAttribute(OidcProfileDefinition.NONCE);
    }

    @JsonIgnore
    public String getAcr() {
        return (String) getAttribute(OidcProfileDefinition.ACR);
    }

    @JsonIgnore
    public Object getAmr() {
        return getAttribute(OidcProfileDefinition.AMR);
    }

    @JsonIgnore
    public String getAzp() {
        return (String) getAttribute(OidcProfileDefinition.AZP);
    }

    public void setAccessToken(final AccessToken accessToken) {
        if (accessToken != null) {
            val accessTokenBase64 = Base64.getEncoder().encodeToString(
                accessToken.toJSONString().getBytes(StandardCharsets.UTF_8));
            addAttribute(OidcProfileDefinition.ACCESS_TOKEN, accessTokenBase64);
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

    @JsonIgnore
    public AccessToken getAccessToken() {
        try {
            val accessTokenObject = getAttribute(OidcProfileDefinition.ACCESS_TOKEN);
            if (accessTokenObject != null) {
                val accessTokenBase64 = accessTokenObject.toString();
                val base64Decoded = new String(Base64.getDecoder().decode(accessTokenBase64), StandardCharsets.UTF_8);
                val accessTokenJSON = new JSONObject(new ObjectMapper().readValue(base64Decoded, Map.class));
                return AccessToken.parse(accessTokenJSON);
            }
            return null;
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @JsonIgnore
    public String getIdTokenString() {
        return (String) getAttribute(OidcProfileDefinition.ID_TOKEN);
    }

    public void setIdTokenString(final String idToken) {
        addAttribute(OidcProfileDefinition.ID_TOKEN, idToken);
    }

    @JsonIgnore
    public JWT getIdToken() {
        if (getIdTokenString() != null) {
            try {
                return JWTParser.parse(getIdTokenString());
            } catch (final ParseException e) {
                throw new OidcException(e);
            }
        } else {
            return null;
        }
    }

    @JsonIgnore
    public RefreshToken getRefreshToken() {
        try {
            val refreshTokenObject = getAttribute(OidcProfileDefinition.REFRESH_TOKEN);
            if (refreshTokenObject != null) {
                return new RefreshToken(refreshTokenObject.toString());
            }
            return null;
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setRefreshToken(final RefreshToken refreshToken) {
        if (refreshToken != null) {
            addAttribute(OidcProfileDefinition.REFRESH_TOKEN, refreshToken.getValue());
        }
    }

    @Override
    public void removeLoginData() {
        removeAttribute(OidcProfileDefinition.ID_TOKEN);
        removeAttribute(OidcProfileDefinition.ACCESS_TOKEN);
        removeAttribute(OidcProfileDefinition.REFRESH_TOKEN);
    }

    @JsonIgnore
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

    @JsonIgnore
    public void setTokenExpirationAdvance(int tokenExpirationAdvance) {
        addAttribute(OidcProfileDefinition.TOKEN_EXPIRATION_ADVANCE, tokenExpirationAdvance);
    }

    @JsonIgnore
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

    @JsonIgnore
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
