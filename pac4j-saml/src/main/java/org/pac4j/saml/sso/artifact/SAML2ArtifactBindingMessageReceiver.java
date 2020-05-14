package org.pac4j.saml.sso.artifact;

import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageReceiver;
import org.pac4j.saml.transport.AbstractPac4jDecoder;
import org.pac4j.saml.util.Configuration;

import java.util.Optional;

/**
 * A message receiver which fetches the actual artifact using SOAP.
 *
 * @since 3.8.0
 */
public class SAML2ArtifactBindingMessageReceiver extends AbstractSAML2MessageReceiver {
    private static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";

    private SAML2MetadataResolver idpMetadataResolver;

    private SAML2MetadataResolver spMetadataResolver;

    private SOAPPipelineProvider soapPipelineProvider;

    public SAML2ArtifactBindingMessageReceiver(final SAML2ResponseValidator validator,
            final SAML2MetadataResolver idpMetadataResolver, final SAML2MetadataResolver spMetadataResolver,
            final SOAPPipelineProvider soapPipelineProvider) {
        super(validator);
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
        this.soapPipelineProvider = soapPipelineProvider;
    }

    @Override
    protected Optional<Endpoint> getEndpoint(SAML2MessageContext context, StatusResponseType response) {
        return Optional.of(
            context.getSPAssertionConsumerService(response)
        );
    }

    @Override
    protected AbstractPac4jDecoder getDecoder(final WebContext webContext) {
        final SAML2ArtifactBindingDecoder decoder = new SAML2ArtifactBindingDecoder(webContext, idpMetadataResolver,
                spMetadataResolver, soapPipelineProvider);
        try {
            decoder.setParserPool(Configuration.getParserPool());
            decoder.initialize();
            decoder.decode();
        } catch (final Exception e) {
            throw new SAMLException("Error decoding SAML message", e);
        }
        return decoder;
    }

    @Override
    protected String getProfileUri() {
        return SAML2_WEBSSO_PROFILE_URI;
    }
}
