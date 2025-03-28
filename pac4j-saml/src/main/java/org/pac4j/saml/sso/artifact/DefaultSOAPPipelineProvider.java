package org.pac4j.saml.sso.artifact;

import lombok.RequiredArgsConstructor;
import net.shibboleth.shared.httpclient.HttpClientBuilder;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.pac4j.saml.client.SAML2Client;

/**
 * A default implementation of {@link SOAPPipelineProvider}, which enforces the
 * default rules set by the SAML SSO Profile.
 *
 * @since 3.8.0
 * @author bidou
 */
@RequiredArgsConstructor
public class DefaultSOAPPipelineProvider implements SOAPPipelineProvider {
    private final SAML2Client client;

    @Override
    public HttpClientBuilder getHttpClientBuilder() {
        return new HttpClientBuilder();
    }

    @Override
    public HttpClientMessagePipelineFactory getPipelineFactory() {
        return new DefaultSOAPPipelineFactory(client.getConfiguration(), client.getIdentityProviderMetadataResolver(),
                client.getServiceProviderMetadataResolver(), client.getSignatureSigningParametersProvider(),
                client.getSignatureTrustEngineProvider(), client.getReplayCache());
    }
}
