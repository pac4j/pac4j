package org.pac4j.oidc.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oidc.profile.azuread.AzureAdAttributesDefinition;

/**
 * <p>This class is the user profile for Azure AD (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.AzureAdClient}.</p>
 *
 * @author Jerome Leleu
 * @version 1.9.0
 */
public class AzureAdProfile extends OidcProfile<AzureAdIdTokenProfile> {

    private static final long serialVersionUID = -8659029290353954198L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new AzureAdAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }


    @Override
    public String getFirstName() {
        return (String) getAttribute(AzureAdAttributesDefinition.GIVEN_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(AzureAdAttributesDefinition.NAME);
    }

    public String getIdp() {
        return (String) getAttribute(AzureAdAttributesDefinition.IDP);
    }

    @Override
    protected AzureAdIdTokenProfile buildJwtIdTokenProfile() {
        return new AzureAdIdTokenProfile();
    }
}
