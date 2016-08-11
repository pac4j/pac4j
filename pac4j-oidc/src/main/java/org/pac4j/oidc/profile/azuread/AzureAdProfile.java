package org.pac4j.oidc.profile.azuread;

import org.pac4j.core.profile.AttributesDefinition;
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

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new AzureAdAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    public String getIdp() {
        return (String) getAttribute(AzureAdAttributesDefinition.IDP);
    }

    public String getOid() {
        return (String) getAttribute(AzureAdAttributesDefinition.OID);
    }

    public String getTid() {
        return (String) getAttribute(AzureAdAttributesDefinition.TID);
    }

    public String getVer() {
        return (String) getAttribute(AzureAdAttributesDefinition.VER);
    }

    public String getUniqueName() {
        return (String) getAttribute(AzureAdAttributesDefinition.UNQIUE_NAME);
    }

    public String getIpaddr() {
        return (String) getAttribute(AzureAdAttributesDefinition.IPADDR);
    }
}
