package org.pac4j.oidc.profile.azuread;

import org.pac4j.core.profile.converter.Converters;

import java.util.Arrays;

/**
 * This class defines the attributes of the Azure AD Id Token OpenID Connect profile.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class AzureAdIdTokenAttributesDefinition extends AzureAdAttributesDefinition {

    public static final String VER = "ver";
    public static final String UNQIUE_NAME = "unique_name";
    public static final String NBF = "nbf";
    public static final String IPADDR = "ipaddr";

    public AzureAdIdTokenAttributesDefinition() {
        super();
        Arrays.stream(new String[] {VER, UNQIUE_NAME, IPADDR}).forEach(a -> primary(a, Converters.STRING));
        primary(NBF, Converters.DATE_TZ_GENERAL);
    }
}
