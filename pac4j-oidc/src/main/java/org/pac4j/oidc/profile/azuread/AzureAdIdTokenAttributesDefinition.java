package org.pac4j.oidc.profile.azuread;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oidc.profile.OidcIdTokenAttributesDefinition;

import java.util.Arrays;

/**
 * This class defines the attributes of the Azure AD Id Token OpenID Connect profile.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class AzureAdIdTokenAttributesDefinition extends OidcIdTokenAttributesDefinition {

    public static final String VER = "ver";
    public static final String UNQIUE_NAME = "unique_name";
    public static final String IPADDR = "ipaddr";

    public AzureAdIdTokenAttributesDefinition() {
        super();
        Arrays.stream(new String[] {AzureAdAttributesDefinition.IDP, AzureAdAttributesDefinition.OID, AzureAdAttributesDefinition.TID,
                VER, UNQIUE_NAME, IPADDR}).forEach(a -> primary(a, Converters.STRING));
    }
}
