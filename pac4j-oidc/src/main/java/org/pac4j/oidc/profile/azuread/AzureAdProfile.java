package org.pac4j.oidc.profile.azuread;

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
}
