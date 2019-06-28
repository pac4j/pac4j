package org.pac4j.saml.sso.artifact;

import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.client.SAML2Client;

public class DefaultSOAPPipelineProvider implements SOAPPipelineProvider {
    private final SAML2Client client;

    public DefaultSOAPPipelineProvider(final SAML2Client client) {
        this.client = client;
    }

    @Override
    public HttpClientBuilder getHttpClientBuilder() {
        return new HttpClientBuilder();
    }

    @Override
    public HttpClientMessagePipelineFactory<SAMLObject, SAMLObject> getPipelineFactory() {
        return new DefaultSOAPPipelineFactory(client.getConfiguration(), client.getIdentityProviderMetadataResolver(),
                client.getServiceProviderMetadataResolver(), client.getSignatureSigningParametersProvider(),
                client.getSignatureTrustEngineProvider(), client.getReplayCache());
    }
}
