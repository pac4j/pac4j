package org.pac4j.oidc.profile.azuread;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

import java.util.Arrays;

/**
 * This class defines the attributes of the Azure AD OpenID Connect profile.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class AzureAdAttributesDefinition extends AttributesDefinition {

    public static final String NAME = "name";
    public static final String GIVEN_NAME = "given_name";
    public static final String FAMILY_NAME = "family_name";
    public static final String EMAIL = "email";
    public static final String IDP = "idp";

    public AzureAdAttributesDefinition() {
        Arrays.stream(new String[] {NAME, GIVEN_NAME, FAMILY_NAME, IDP, EMAIL}).forEach(a -> primary(a, Converters.STRING));
    }
}
