package org.pac4j.oidc.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.RelativeUrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.client.azuread.AzureAdResourceRetriever;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.azuread.AzureAdProfile;
import org.pac4j.oidc.profile.azuread.AzureAdProfileCreator;

/**
 * A specialized {@link OidcClient} for authenticating againt Microsoft Azure AD. Microsoft Azure
 * AD provides authentication for multiple tenants, or, when the tenant is not known prior to
 * authentication, the speciall common-tenant. For a specific tenant, the following discovery URI
 * must be used:
 * {@code https://login.microsoftonline.com/tenantid/.well-known/openid-configuration} or
 * {@code https://login.microsoftonline.com/tenantid/v2.0/.well-known/openid-configuration} for
 * Azure AD v2.0. Replace {@code tenantid} with the ID of the tenant to authenticate against. To
 * find this ID, fill in your tenant's domain name. Your tenant ID is the UUID in
 * {@code authorization_endpoint}.
 * <p>
 * For authentication against an unknown (or dynamic tenant), use {@code common} as ID.
 * Authentication against the common endpoint results in a ID token with a {@code issuer} different
 * from the {@code issuer} mentioned in the discovery data. This class uses to special validator
 * to correctly validate the issuer returned by Azure AD.
 * <p>
 * More information at: https://msdn.microsoft.com/en-us/library/azure/dn645541.aspx
 *
 * @author Emond Papegaaij
 * @since 1.8.3
 */
public class AzureAdClient extends OidcClient<AzureAdProfile> {

    public AzureAdClient() {
    }

    public AzureAdClient(final OidcConfiguration configuration) {
        super(configuration);
        // https://github.com/apereo/cas/issues/2711
        // don't include clientName because its ignored and causes validation failure
        setIncludeClientNameInCallbackUrl(false);
    }

    @Override
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", getConfiguration());
        getConfiguration().setResourceRetriever(new AzureAdResourceRetriever());
        defaultProfileCreator(new AzureAdProfileCreator(getConfiguration()));
        setUrlResolver(new AzureAdClientUrlResolver("/cas/delegatedAuthn/oidc/" + getName()));
        super.clientInit(context);
    }

    private static class AzureAdClientUrlResolver extends RelativeUrlResolver {
        private final String contextPath;

        AzureAdClientUrlResolver(String contextPath) {
            this.contextPath = contextPath;
        }

        @Override
        public String compute(String url, WebContext context) {
            return super.compute(contextPath, context);
        }
    }
}
