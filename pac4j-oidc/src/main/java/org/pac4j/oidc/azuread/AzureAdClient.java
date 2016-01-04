package org.pac4j.oidc.azuread;

import org.pac4j.oidc.client.OidcClient;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.http.ResourceRetriever;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;

/**
 * A specialized {@link OidcClient} for authenticating againt Microsoft Azure AD. Microsoft Azure
 * AD provides authentication for multiple tenants, or, when the tenant is not known prior to
 * authentication, the speciall common-tenant. For a specific tenant, the following discovery URI
 * must be used:
 * {@code https://login.microsoftonline.com/tenantid/.well-known/openid-configuration} or
 * {@code https://login.microsoftonline.com/tenantid/v2.0/.well-known/openid-configuration} for the
 * Azure AD v2.0 preview. Replace {@code tenantid} with the ID of the tenant to authenticate
 * against. To find this ID, fill in your tenant's domain name. Your tenant ID is the UUID in
 * {@code authorization_endpoint}.
 * 
 * For authentication against an unknown (or dynamic tenant), use {@code common} as ID.
 * Authentication against the common endpoint results in a ID token with a {@code issuer} different
 * from the {@code issuer} mentioned in the discovery data. This class uses to special validator
 * to correctly validate the issuer returned by Azure AD.
 * 
 * @author Emond Papegaaij
 * @since 1.8.3
 */
public class AzureAdClient extends OidcClient {
    public AzureAdClient() {
    }
	
    public AzureAdClient(final String clientId, final String secret, final String discoveryURI) {
        super(clientId, secret, discoveryURI);
    }
	
	protected IDTokenValidator createRSATokenValidator(JWSAlgorithm jwsAlgorithm, ClientID clientID) throws java.net.MalformedURLException {
		return new AzureAdIdTokenValidator(super.createRSATokenValidator(jwsAlgorithm, clientID));
	}
	
	@Override
	protected ResourceRetriever createResourceRetriever() {
		return new AzureAdResourceRetriever();
	}
}
