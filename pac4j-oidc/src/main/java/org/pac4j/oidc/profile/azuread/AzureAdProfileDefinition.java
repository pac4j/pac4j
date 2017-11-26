package org.pac4j.oidc.profile.azuread;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oidc.profile.OidcProfileDefinition;

import java.util.Arrays;

/**
 * This class defines the attributes of the Azure AD OpenID Connect profile.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class AzureAdProfileDefinition extends OidcProfileDefinition {

    public static final String IDP = "idp";
    public static final String OID = "oid";
    public static final String TID = "tid";
    public static final String VER = "ver";
    public static final String UNQIUE_NAME = "unique_name";
    public static final String IPADDR = "ipaddr";
    public static final String UPN = "upn";

    public AzureAdProfileDefinition() {
        super();
        Arrays.stream(new String[] {IDP, OID, TID, VER, UNQIUE_NAME, IPADDR, UPN}).forEach(a -> primary(a, Converters.STRING));
        setProfileFactory(x -> new AzureAdProfile());
    }
}
