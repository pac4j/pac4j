package org.pac4j.oidc.profile.azuread;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oidc.profile.JwtIdTokenProfile;

import java.util.Date;

/**
 * Azure profile for the ID Token.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class AzureAdIdTokenProfile extends AzureAdProfile implements JwtIdTokenProfile {

    private static final long serialVersionUID = 703886892025829652L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new AzureAdIdTokenAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    public String getVer() {
        return (String) getAttribute(AzureAdIdTokenAttributesDefinition.VER);
    }

    public String getUniqueName() {
        return (String) getAttribute(AzureAdIdTokenAttributesDefinition.UNQIUE_NAME);
    }

    public Date getNbf() {
        return (Date) getAttribute(AzureAdIdTokenAttributesDefinition.NBF);
    }

    public String getIpaddr() {
        return (String) getAttribute(AzureAdIdTokenAttributesDefinition.IPADDR);
    }
}
