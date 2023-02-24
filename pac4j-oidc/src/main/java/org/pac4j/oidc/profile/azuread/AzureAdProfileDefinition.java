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

    /** Constant <code>IDP="idp"</code> */
    public static final String IDP = "idp";
    /** Constant <code>OID="oid"</code> */
    public static final String OID = "oid";
    /** Constant <code>TID="tid"</code> */
    public static final String TID = "tid";
    /** Constant <code>VER="ver"</code> */
    public static final String VER = "ver";
    /** Constant <code>UNQIUE_NAME="unique_name"</code> */
    public static final String UNQIUE_NAME = "unique_name";
    /** Constant <code>IPADDR="ipaddr"</code> */
    public static final String IPADDR = "ipaddr";
    /** Constant <code>UPN="upn"</code> */
    public static final String UPN = "upn";

    /**
     * <p>Constructor for AzureAdProfileDefinition.</p>
     */
    public AzureAdProfileDefinition() {
        super();
        Arrays.stream(new String[] {IDP, OID, TID, VER, UNQIUE_NAME, IPADDR, UPN}).forEach(a -> primary(a, Converters.STRING));
        setProfileFactory(x -> new AzureAdProfile());
    }
}
