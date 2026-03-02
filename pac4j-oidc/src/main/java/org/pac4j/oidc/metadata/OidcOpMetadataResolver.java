package org.pac4j.oidc.metadata;

import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.resource.SpringResourceLoader;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.clientauth.ClientAuthenticationBuilder;
import org.pac4j.oidc.credentials.clientauth.DefaultClientAuthenticationBuilder;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * The metadata resolver for the OIDC OP.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@Slf4j
public class OidcOpMetadataResolver extends SpringResourceLoader<OIDCProviderMetadata> implements IOidcOpMetadataResolver {

    protected final OidcConfiguration configuration;

    protected ClientAuthenticationBuilder clientAuthenticationBuilder;

    @Getter
    protected TokenValidator tokenValidator;

    /**
     * <p>Constructor for OidcOpMetadataResolver.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     */
    public OidcOpMetadataResolver(final OidcConfiguration configuration) {
        super(buildResource(configuration));
        this.configuration = configuration;
    }

    private static Resource buildResource(final OidcConfiguration configuration) {
        if (configuration != null && configuration.getDiscoveryURI() != null) {
            var resource = SpringResourceHelper.buildResourceFromPath(configuration.getDiscoveryURI());
            if (resource instanceof final UrlResource urlResource) {
                return new OidcMetadataUrlResource(urlResource.getURL(), configuration);
            } else {
                return resource;
            }
        }
        return null;
    }

    @Override
    protected void internalLoad() {
        this.loaded = retrieveMetadata();

        this.clientAuthenticationBuilder = new DefaultClientAuthenticationBuilder(this.configuration, this.loaded);
        this.clientAuthenticationBuilder.buildClientAuthentication();

        this.tokenValidator = createTokenValidator();
    }

    /**
     * <p>retrieveMetadata.</p>
     *
     * @return a {@link OIDCProviderMetadata} object
     */
    protected OIDCProviderMetadata retrieveMetadata() {
        try (val in = SpringResourceHelper.getResourceInputStream(
            resource,
            null,
            configuration.getSslSocketFactory(),
            configuration.getHostnameVerifier(),
            configuration.getConnectTimeout(),
            configuration.getReadTimeout()
        )) {
            val metadata = IOUtils.readInputStreamToString(in);
            return OIDCProviderMetadata.parse(metadata);
        } catch (final IOException | ParseException e) {
            throw new OidcException("Error getting OP metadata", e);
        }
    }

    protected TokenValidator createTokenValidator() {
        return new TokenValidator(configuration, this.loaded);
    }

    public ClientAuthentication getClientAuthentication() {
        return clientAuthenticationBuilder.getClientAuthentication();
    }

    @EqualsAndHashCode(callSuper = true)
    private static class OidcMetadataUrlResource extends UrlResource {
        private final OidcConfiguration configuration;

        public OidcMetadataUrlResource(final URL url, final OidcConfiguration configuration) {
            super(url);
            this.configuration = configuration;
        }

        @Override
        protected void customizeConnection(final URLConnection connection) throws IOException {
            if (connection instanceof final HttpsURLConnection httpsConnection) {
                if (configuration.getHostnameVerifier() != null) {
                    httpsConnection.setHostnameVerifier(configuration.getHostnameVerifier());
                }
                if (configuration.getSslSocketFactory() != null) {
                    httpsConnection.setSSLSocketFactory(configuration.getSslSocketFactory());
                }
            }
            super.customizeConnection(connection);
        }
    }
}
