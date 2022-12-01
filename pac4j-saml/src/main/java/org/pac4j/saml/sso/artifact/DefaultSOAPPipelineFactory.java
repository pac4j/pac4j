package org.pac4j.saml.sso.artifact;


import lombok.val;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.impl.*;
import org.opensaml.messaging.pipeline.httpclient.BasicHttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.saml.common.binding.impl.*;
import org.opensaml.saml.common.binding.security.impl.*;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder.SAML1Version;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.saml2.binding.decoding.impl.HttpClientResponseSOAP11Decoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HttpClientRequestSOAP11Encoder;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xmlsec.impl.BasicSignatureValidationParametersResolver;
import org.opensaml.xmlsec.messaging.impl.PopulateSignatureValidationParametersHandler;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation of the pipeline factory, which enforces the rules
 * set by the web SSO profile. To add additional handlers, you can override
 * {@link #getInboundHandlers()}, {@link #getOutboundPayloadHandlers()} and/or
 * {@link #getOutboundTransportHandlers()}. To modify the configuration of a
 * specific handler, override the build method for that handler.
 *
 * @since 3.8.0
 */
@SuppressWarnings("unchecked")
public class DefaultSOAPPipelineFactory implements HttpClientMessagePipelineFactory {
    protected final SAML2Configuration configuration;

    protected final SAML2MetadataResolver idpMetadataResolver;

    protected final SAML2MetadataResolver spMetadataResolver;

    protected final SignatureSigningParametersProvider signingParametersProvider;

    protected final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected final ReplayCacheProvider replayCache;

    public DefaultSOAPPipelineFactory(final SAML2Configuration configuration,
                                      final SAML2MetadataResolver idpMetadataResolver,
                                      final SAML2MetadataResolver spMetadataResolver,
                                      final SignatureSigningParametersProvider signingParametersProvider,
                                      final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider,
                                      final ReplayCacheProvider replayCache) {
        this.configuration = configuration;
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
        this.signingParametersProvider = signingParametersProvider;
        this.signatureTrustEngineProvider = signatureTrustEngineProvider;
        this.replayCache = replayCache;
    }

    protected List<MessageHandler> getInboundHandlers() throws ComponentInitializationException {
        final List<MessageHandler> handlers = new ArrayList<>();
        handlers.add(buildSAMLProtocolAndRoleHandler(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        handlers.add(buildSAMLMetadataLookupHandler(idpMetadataResolver));
        handlers.add(buildSchemaValidateXMLMessage());
        handlers.add(buildCheckMessageVersionHandler());
        handlers.add(buildMessageLifetimeSecurityHandler());
        handlers.add(buildInResponseToSecurityHandler());
        handlers.add(buildMessageReplaySecurityHandler());
        handlers.add(buildCheckMandatoryIssuer());
        handlers.add(buildCheckExpectedIssuer());
        handlers.add(buildPopulateSignatureSigningParametersHandler());
        handlers.add(buildPopulateSignatureValidationParametersHandler());
        handlers.add(buildSAMLProtocolMessageXMLSignatureSecurityHandler());
        handlers.add(buildCheckAndRecordServerTLSEntityAuthenticationtHandler());
        handlers.add(buildCheckMandatoryAuthentication());
        handlers.add(buildSAMLSOAPDecoderBodyHandler());
        return handlers;
    }

    protected List<MessageHandler> getOutboundPayloadHandlers() throws ComponentInitializationException {
        final List<MessageHandler> handlers = new ArrayList<>();
        handlers.add(buildSAMLProtocolAndRoleHandler(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        handlers.add(buildSAMLMetadataLookupHandler(spMetadataResolver));
        handlers.add(buildPopulateSignatureSigningParametersHandler());
        handlers.add(buildSAMLOutboundProtocolMessageSigningHandler());
        return handlers;
    }

    protected List<MessageHandler> getOutboundTransportHandlers() throws ComponentInitializationException {
        return new ArrayList<>();
    }

    protected MessageHandler buildSAMLProtocolAndRoleHandler(final QName roleName)
        throws ComponentInitializationException {
        val protocolAndRoleHandler = new SAMLProtocolAndRoleHandler();
        protocolAndRoleHandler.setProtocol(SAMLConstants.SAML20P_NS);
        protocolAndRoleHandler.setRole(roleName);
        protocolAndRoleHandler.initialize();
        return protocolAndRoleHandler;
    }

    protected MessageHandler buildSAMLMetadataLookupHandler(final SAML2MetadataResolver metadataResolver)
        throws ComponentInitializationException {
        val roleResolver = new PredicateRoleDescriptorResolver(metadataResolver.resolve());
        roleResolver.initialize();

        val metadataLookupHandler = new SAMLMetadataLookupHandler();
        metadataLookupHandler.setRoleDescriptorResolver(roleResolver);
        metadataLookupHandler.initialize();
        return metadataLookupHandler;
    }

    protected MessageHandler buildSchemaValidateXMLMessage() throws ComponentInitializationException {
        try {
            val validateXMLHandler = new SchemaValidateXMLMessage(
                new SAMLSchemaBuilder(SAML1Version.SAML_11).getSAMLSchema());
            validateXMLHandler.initialize();
            return validateXMLHandler;
        } catch (final SAXException e) {
            throw new ComponentInitializationException(e);
        }
    }

    protected MessageHandler buildCheckMessageVersionHandler() throws ComponentInitializationException {
        val messageVersionHandler = new CheckMessageVersionHandler();
        messageVersionHandler.initialize();
        return messageVersionHandler;
    }

    protected MessageHandler buildMessageLifetimeSecurityHandler() throws ComponentInitializationException {
        val lifetimeHandler = new MessageLifetimeSecurityHandler();
        lifetimeHandler.setClockSkew(Duration.ofMillis(configuration.getAcceptedSkew() * 1000));
        lifetimeHandler.initialize();
        return lifetimeHandler;
    }

    protected MessageHandler buildInResponseToSecurityHandler() throws ComponentInitializationException {
        val inResponseToHandler = new InResponseToSecurityHandler();
        inResponseToHandler.initialize();
        return inResponseToHandler;
    }

    protected MessageHandler buildMessageReplaySecurityHandler() throws ComponentInitializationException {
        val messageReplayHandler = new MessageReplaySecurityHandler();
        messageReplayHandler.setExpires(Duration.ofMillis(configuration.getAcceptedSkew() * 1000));
        messageReplayHandler.setReplayCache(replayCache.get());
        messageReplayHandler.initialize();
        return messageReplayHandler;
    }

    protected MessageHandler buildCheckMandatoryIssuer() throws ComponentInitializationException {
        val mandatoryIssuer = new CheckMandatoryIssuer();
        mandatoryIssuer.setIssuerLookupStrategy(new IssuerFunction());
        mandatoryIssuer.initialize();
        return mandatoryIssuer;
    }

    protected MessageHandler buildCheckExpectedIssuer() throws ComponentInitializationException {
        val expectedIssuer = new CheckExpectedIssuer();
        expectedIssuer.setIssuerLookupStrategy(new IssuerFunction());
        expectedIssuer.setExpectedIssuerLookupStrategy(messageContext -> idpMetadataResolver.getEntityId());
        expectedIssuer.initialize();
        return expectedIssuer;
    }

    protected MessageHandler buildPopulateSignatureSigningParametersHandler()
        throws ComponentInitializationException {
        val signatureSigningParameters = new PopulateSignatureSigningParametersHandler();
        signatureSigningParameters.setSignatureSigningParametersResolver(
            new DefaultSignatureSigningParametersResolver(signingParametersProvider));
        signatureSigningParameters.initialize();
        return signatureSigningParameters;
    }

    protected MessageHandler buildPopulateSignatureValidationParametersHandler()
        throws ComponentInitializationException {
        val signatureValidationParameters =
            new PopulateSignatureValidationParametersHandler();
        signatureValidationParameters
            .setSignatureValidationParametersResolver(new BasicSignatureValidationParametersResolver() {
                @Override
                protected SignatureTrustEngine resolveSignatureTrustEngine(final CriteriaSet criteria) {
                    return signatureTrustEngineProvider.build();
                }
            });
        signatureValidationParameters.initialize();
        return signatureValidationParameters;
    }

    protected MessageHandler buildSAMLProtocolMessageXMLSignatureSecurityHandler()
        throws ComponentInitializationException {
        val messageXMLSignatureHandler =
            new SAMLProtocolMessageXMLSignatureSecurityHandler();
        messageXMLSignatureHandler.initialize();
        return messageXMLSignatureHandler;
    }

    protected MessageHandler buildCheckAndRecordServerTLSEntityAuthenticationtHandler()
        throws ComponentInitializationException {
        val tlsHandler =
            new CheckAndRecordServerTLSEntityAuthenticationtHandler();
        tlsHandler.initialize();
        return tlsHandler;
    }

    protected MessageHandler buildCheckMandatoryAuthentication() {
        val mandatoryAuthentication = new CheckMandatoryAuthentication();
        mandatoryAuthentication.setAuthenticationLookupStrategy(
            context -> context.getSubcontext(SAMLPeerEntityContext.class).isAuthenticated());
        return mandatoryAuthentication;
    }

    protected MessageHandler buildSAMLSOAPDecoderBodyHandler() throws ComponentInitializationException {
        val soapDecoderBody = new SAMLSOAPDecoderBodyHandler();
        soapDecoderBody.initialize();
        return soapDecoderBody;
    }

    protected MessageHandler buildSAMLOutboundProtocolMessageSigningHandler()
        throws ComponentInitializationException {
        val messageSigner = new SAMLOutboundProtocolMessageSigningHandler();
        messageSigner.initialize();
        return messageSigner;
    }

    protected BasicMessageHandlerChain toHandlerChain(final List<MessageHandler> handlers) {
        val ret = new BasicMessageHandlerChain();
        ret.setHandlers(handlers);
        return ret;
    }

    @Override
    public HttpClientMessagePipeline newInstance() {
        val ret = new BasicHttpClientMessagePipeline(
            new HttpClientRequestSOAP11Encoder(), new HttpClientResponseSOAP11Decoder());
        try {
            ret.setInboundHandler(toHandlerChain(getInboundHandlers()));
            ret.setOutboundPayloadHandler(toHandlerChain(getOutboundPayloadHandlers()));
            ret.setOutboundTransportHandler(toHandlerChain(getOutboundTransportHandlers()));
        } catch (final ComponentInitializationException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public HttpClientMessagePipeline newInstance(final String pipelineName) {
        return newInstance();
    }
}
