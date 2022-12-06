package org.pac4j.saml.sso.artifact;

import lombok.val;
import net.shibboleth.shared.httpclient.HttpClientBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.impl.DefaultEndpointResolver;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.http.PipelineFactoryHttpSOAPClient;
import org.opensaml.soap.common.SOAPException;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.transport.AbstractPac4jDecoder;
import org.pac4j.saml.transport.Pac4jHTTPArtifactDecoder;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Decodes a SAML artifact binding request by fetching the actual artifact via
 * SOAP.
 *
 * @since 3.8.0
 */
public class SAML2ArtifactBindingDecoder extends AbstractPac4jDecoder {
    private final SAML2MetadataResolver idpMetadataResolver;

    private final SAML2MetadataResolver spMetadataResolver;

    private final SOAPPipelineProvider soapPipelineProvider;

    public SAML2ArtifactBindingDecoder(final WebContext context, final SAML2MetadataResolver idpMetadataResolver,
            final SAML2MetadataResolver spMetadataResolver, final SOAPPipelineProvider soapPipelineProvider) {
        super(context);
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
        this.soapPipelineProvider = soapPipelineProvider;
    }

    @Override
    public String getBindingURI(final SAML2MessageContext messageContext) {
        return SAMLConstants.SAML2_ARTIFACT_BINDING_URI;
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        try {
            val endpointResolver = new DefaultEndpointResolver<ArtifactResolutionService>();
            endpointResolver.initialize();

            val roleResolver = new PredicateRoleDescriptorResolver(
                    idpMetadataResolver.resolve());
            roleResolver.initialize();

            val messageContext = new SAML2MessageContext();

            val soapClient = new PipelineFactoryHttpSOAPClient() {
                @SuppressWarnings("rawtypes")
                @Override
                public void send(final String endpoint, final InOutOperationContext operationContext)
                        throws SOAPException, SecurityException {
                    super.send(endpoint, operationContext);
                    transferContext(operationContext, messageContext);
                }
            };

            /**
             * SAML integration with HTTP-artifact binding
             *
             * Since CAS does not support artifact binding, I come across an issue trying to perform the back-end channel artifact resolution request over to IDP entity that only supports HTTP-artifact binding.
             * The issue is that the two-way SSL handshake does not occur because we are not sending our certificate over once they request for our certificate for validation.
             * The solution below attempts to create a brand new SSLConnectionSocketFactory object,
             * where the correct keystore and truststore values are specified. This way, we should be able to correctly complete the two-way handshake for the artifact resolution request.
             *
             */
            KeyStore keyStore = KeyStore.getInstance("JKS");
            Optional map = this.context.getRequestAttribute("props");

            if(map.isPresent()) {
                logger.debug("Creating custom SSL Connection socket");

                HashMap<String, String> propertiesMap = (HashMap) map.get();

                InputStream keystoreStream = new FileInputStream(propertiesMap.get("keystore-path"));
                String keystorePassword = propertiesMap.get("keystore-password");
                keyStore.load(keystoreStream, keystorePassword.toCharArray());
                keystoreStream.close();

                HttpClientBuilder httpClientBuilder = soapPipelineProvider.getHttpClientBuilder();

                SSLContextBuilder sslContextBuilder = SSLContexts.custom();
                sslContextBuilder = sslContextBuilder.loadTrustMaterial(new File(propertiesMap.get("truststore-path")), propertiesMap.get("truststore-password").toCharArray());
                sslContextBuilder = sslContextBuilder.loadKeyMaterial(keyStore , keystorePassword.toCharArray());
                SSLContext sslContextNew = sslContextBuilder.build();

                SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslContextNew, new String[]{"TLSv1.2"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
                httpClientBuilder.setTLSSocketFactory(sslConSocFactory);
                soapClient.setPipelineFactory(soapPipelineProvider.getPipelineFactory());
                soapClient.setHttpClient(httpClientBuilder.buildClient());

            } else {
                //else statement contains the original code from this pac4j class.
                logger.debug("props request attribute was not found, proceeding with default soap request without any customized SSL Connection socket.");
                soapClient.setPipelineFactory(soapPipelineProvider.getPipelineFactory());
                soapClient.setHttpClient(soapPipelineProvider.getHttpClientBuilder().buildClient());
            }
            val artifactDecoder = new Pac4jHTTPArtifactDecoder();
            artifactDecoder.setWebContext(context);
            artifactDecoder.setSelfEntityIDResolver(new FixedEntityIdResolver(spMetadataResolver));
            artifactDecoder.setRoleDescriptorResolver(roleResolver);
            artifactDecoder.setArtifactEndpointResolver(endpointResolver);
            artifactDecoder.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
            artifactDecoder.setSOAPClient(soapClient);
            artifactDecoder.setParserPool(getParserPool());
            artifactDecoder.initialize();
            artifactDecoder.decode();

            messageContext.getMessageContext().setMessage(artifactDecoder.getMessageContext().getMessage());

            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext.getMessageContext());
        } catch (final Exception e) {
            throw new MessageDecodingException(e);
        }
    }

    protected void transferContext(final InOutOperationContext operationContext, final SAML2MessageContext messageContext) {
        messageContext.getMessageContext()
                .addSubcontext(Objects.requireNonNull(Objects.requireNonNull(operationContext.getInboundMessageContext())
                    .getSubcontext(SAMLPeerEntityContext.class)));
    }
}
