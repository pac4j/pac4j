package org.pac4j.oidc.profile.azuread;

import org.pac4j.oidc.profile.OidcProfile;

import java.io.Serial;

/**
 * <p>This class is the user profile for Azure AD (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.AzureAd2Client}.</p>
 *
 * @author Jerome Leleu
 * @version 1.9.0
 */
public class AzureAdProfile extends OidcProfile {

    @Serial
    private static final long serialVersionUID = -8659029290353954198L;

    /**
     * <p>getIdp.</p>
     *
     * @return a {@link String} object
     */
    public String getIdp() {
        return (String) getAttribute(AzureAdProfileDefinition.IDP);
    }

    /**
     * <p>getOid.</p>
     *
     * @return a {@link String} object
     */
    public String getOid() {
        return (String) getAttribute(AzureAdProfileDefinition.OID);
    }

    /**
     * <p>getTid.</p>
     *
     * @return a {@link String} object
     */
    public String getTid() {
        return (String) getAttribute(AzureAdProfileDefinition.TID);
    }

    /**
     * <p>getVer.</p>
     *
     * @return a {@link String} object
     */
    public String getVer() {
        return (String) getAttribute(AzureAdProfileDefinition.VER);
    }

    /**
     * <p>getUniqueName.</p>
     *
     * @return a {@link String} object
     */
    public String getUniqueName() {
        return (String) getAttribute(AzureAdProfileDefinition.UNQIUE_NAME);
    }

    /**
     * <p>getIpaddr.</p>
     *
     * @return a {@link String} object
     */
    public String getIpaddr() {
        return (String) getAttribute(AzureAdProfileDefinition.IPADDR);
    }

    /**
     * <p>getUpn.</p>
     *
     * @return a {@link String} object
     */
    public String getUpn() {
        return (String) getAttribute(AzureAdProfileDefinition.UPN);
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(AzureAdProfileDefinition.UPN);
    }
}
