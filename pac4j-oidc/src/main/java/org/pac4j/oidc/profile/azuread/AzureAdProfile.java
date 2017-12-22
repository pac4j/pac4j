package org.pac4j.oidc.profile.azuread;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.profile.OidcProfile;

/**
 * <p>This class is the user profile for Azure AD (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.AzureAdClient}.</p>
 *
 * @author Jerome Leleu
 * @version 1.9.0
 */
public class AzureAdProfile extends OidcProfile {

    private static final long serialVersionUID = -8659029290353954198L;

    private int idTokenExpireAdvance;

    public AzureAdProfile() {
        this.idTokenExpireAdvance = 10;
    }

    public AzureAdProfile(int idTokenExpireAdvance) {
        this.idTokenExpireAdvance = idTokenExpireAdvance;
    }

    public String getIdp() {
        return (String) getAttribute(AzureAdProfileDefinition.IDP);
    }

    public String getOid() {
        return (String) getAttribute(AzureAdProfileDefinition.OID);
    }

    public String getTid() {
        return (String) getAttribute(AzureAdProfileDefinition.TID);
    }

    public String getVer() {
        return (String) getAttribute(AzureAdProfileDefinition.VER);
    }

    public String getUniqueName() {
        return (String) getAttribute(AzureAdProfileDefinition.UNQIUE_NAME);
    }

    public String getIpaddr() {
        return (String) getAttribute(AzureAdProfileDefinition.IPADDR);
    }

    public String getUpn() {
        return (String) getAttribute(AzureAdProfileDefinition.UPN);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(AzureAdProfileDefinition.UPN);
    }

    @Override
    public boolean isExpired() {
        try {
            JWT jwt = this.getIdToken();
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date expiresOn = claims.getExpirationTime();

            Calendar now = Calendar.getInstance();
            now.add( Calendar.SECOND, idTokenExpireAdvance );

            if (expiresOn.before(now.getTime())) {
                return true;
            }
        } catch (ParseException e) {
            throw new TechnicalException(e);
        }

        return false;
    }
}
