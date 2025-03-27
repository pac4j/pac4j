package org.pac4j.saml.client;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2ContextProvider;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.authenticator.SAML2Authenticator;
import org.pac4j.saml.credentials.extractor.SAML2CredentialsExtractor;
import org.pac4j.saml.crypto.*;
import org.pac4j.saml.logout.SAML2LogoutActionBuilder;
import org.pac4j.saml.logout.impl.SAML2LogoutRequestMessageSender;
import org.pac4j.saml.logout.impl.SAML2LogoutValidator;
import org.pac4j.saml.logout.processor.SAML2LogoutProcessor;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.redirect.SAML2RedirectionActionBuilder;
import org.pac4j.saml.replay.InMemoryReplayCacheProvider;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.sso.artifact.DefaultSOAPPipelineProvider;
import org.pac4j.saml.sso.artifact.SOAPPipelineProvider;
import org.pac4j.saml.sso.impl.SAML2AuthnResponseValidator;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageSender;
import org.pac4j.saml.state.SAML2StateGenerator;
import org.pac4j.saml.util.Configuration;

import java.io.Closeable;
import java.io.IOException;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * This class is the client to authenticate and logout users with a SAML2 Identity Provider.
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @author Ruochao Zheng
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class SAML2Client extends IndirectClient implements Closeable {

    @Getter
    @Setter
    protected SAMLContextProvider contextProvider;

    @Getter
    @Setter
    protected SignatureSigningParametersProvider signatureSigningParametersProvider;

    @Getter
    @Setter
    protected SAML2ResponseValidator authnResponseValidator;

    @Getter
    @Setter
    protected SAML2LogoutValidator logoutValidator;

    @Getter
    @Setter
    protected SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    @Getter
    @Setter
    protected SAML2MetadataResolver identityProviderMetadataResolver;

    @Getter
    @Setter
    protected SAML2MetadataResolver serviceProviderMetadataResolver;

    @Getter
    @Setter
    protected Decrypter decrypter;

    @Getter
    @Setter
    protected SAML2Configuration configuration;

    @Getter
    @Setter
    protected ValueGenerator stateGenerator = new SAML2StateGenerator(this);

    @Getter
    @Setter
    protected ReplayCacheProvider replayCache;

    @Getter
    @Setter
    protected SOAPPipelineProvider soapPipelineProvider;

    static {
        assertNotNull("parserPool", Configuration.getParserPool());
        assertNotNull("marshallerFactory", Configuration.getMarshallerFactory());
        assertNotNull("unmarshallerFactory", Configuration.getUnmarshallerFactory());
        assertNotNull("builderFactory", Configuration.getBuilderFactory());
    }

    /**
     * <p>Constructor for SAML2Client.</p>
     */
    public SAML2Client() { }

    /**
     * <p>Constructor for SAML2Client.</p>
     *
     * @param configuration a {@link SAML2Configuration} object
     */
    public SAML2Client(final SAML2Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("configuration", this.configuration);

        // First of all, initialize the configuration. It may dynamically load some properties, if it is not a static one.
        val callbackUrl = computeFinalCallbackUrl(null);
        configuration.setCallbackUrl(callbackUrl);
        configuration.init(forceReinit);

        initDecrypter();
        initSignatureSigningParametersProvider();
        initIdentityProviderMetadataResolver(forceReinit);
        initServiceProviderMetadataResolver();
        initSAMLContextProvider();
        initSignatureTrustEngineProvider();
        initSAMLReplayCache();
        initSAMLResponseValidator();
        initSOAPPipelineProvider();
        initSAMLLogoutResponseValidator();
        initRedirectActionBuilder();
        initCredentialExtractor();
        initAuthenticator();
        initLogoutProcessor();
        initLogoutActionBuilder();
    }

    private void initLogoutActionBuilder() {
        if (getLogoutActionBuilder() == null) {
            setLogoutActionBuilderIfUndefined(new SAML2LogoutActionBuilder(this));
        }
    }

    private void initRedirectActionBuilder() {
        if (getRedirectionActionBuilder() == null) {
            setRedirectionActionBuilderIfUndefined(new SAML2RedirectionActionBuilder(this));
        }
    }

    private void initCredentialExtractor() {
        if (getCredentialsExtractor() == null) {
            setCredentialsExtractorIfUndefined(new SAML2CredentialsExtractor(this, this.identityProviderMetadataResolver,
                this.serviceProviderMetadataResolver, this.soapPipelineProvider));
        }
    }

    private void initAuthenticator() {
        if (getAuthenticator() == null) {
            setAuthenticatorIfUndefined(new SAML2Authenticator(authnResponseValidator, this.logoutValidator,
                this.configuration.getAttributeAsId(), this.configuration.getMappedAttributes()));
        }
    }

    private void initLogoutProcessor() {
        if (getLogoutProcessor() == null) {
            setLogoutProcessor(new SAML2LogoutProcessor(this));
        }
    }

    /**
     * <p>initSOAPPipelineProvider.</p>
     */
    protected void initSOAPPipelineProvider() {
        if (soapPipelineProvider == null) {
            this.soapPipelineProvider = new DefaultSOAPPipelineProvider(this);
        }
    }

    /**
     * <p>getLogoutRequestMessageSender.</p>
     *
     * @return a {@link SAML2LogoutRequestMessageSender} object
     */
    public SAML2LogoutRequestMessageSender getLogoutRequestMessageSender() {
        return new SAML2LogoutRequestMessageSender(this.signatureSigningParametersProvider,
            this.configuration.getSpLogoutRequestBindingType(), false,
            this.configuration.isSpLogoutRequestSigned());
    }

    /**
     * <p>getSSOMessageSender.</p>
     *
     * @return a {@link SAML2WebSSOMessageSender} object
     */
    public SAML2WebSSOMessageSender getSSOMessageSender() {
        return new SAML2WebSSOMessageSender(this.signatureSigningParametersProvider,
            this.configuration.getAuthnRequestBindingType(), true,
            this.configuration.isAuthnRequestSigned());
    }

    /**
     * <p>initSAMLLogoutResponseValidator.</p>
     */
    protected void initSAMLLogoutResponseValidator() {
        if (logoutValidator == null) {
            this.logoutValidator = new SAML2LogoutValidator(this.signatureTrustEngineProvider,
                this.decrypter, findSessionLogoutHandler(),
                this.replayCache, this.configuration.getUriComparator());
            this.logoutValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
            this.logoutValidator.setPartialLogoutTreatedAsSuccess(this.configuration.isPartialLogoutTreatedAsSuccess());
        }
    }

    /**
     * <p>initSAMLResponseValidator.</p>
     */
    protected void initSAMLResponseValidator() {
        // Build the SAML response validator
        if (authnResponseValidator == null) {
            this.authnResponseValidator = new SAML2AuthnResponseValidator(
                this.signatureTrustEngineProvider,
                this.decrypter,
                this.replayCache,
                this.configuration,
                findSessionLogoutHandler());
            this.authnResponseValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
        }
    }

    /**
     * <p>initSignatureTrustEngineProvider.</p>
     */
    protected void initSignatureTrustEngineProvider() {
        // Build provider for digital signature validation and encryption
        if (signatureTrustEngineProvider == null) {
            this.signatureTrustEngineProvider = new ExplicitSignatureTrustEngineProvider(
                this.identityProviderMetadataResolver, this.serviceProviderMetadataResolver);
            if (this.configuration.isAllSignatureValidationDisabled()) {
                this.signatureTrustEngineProvider = new LogOnlySignatureTrustEngineProvider(this.signatureTrustEngineProvider);
            }
        }
    }

    /**
     * <p>initSAMLContextProvider.</p>
     */
    protected void initSAMLContextProvider() {
        if (this.contextProvider == null) {
            this.contextProvider = new SAML2ContextProvider(this.identityProviderMetadataResolver, this.serviceProviderMetadataResolver,
                this.configuration.getSamlMessageStoreFactory());
        }
    }

    /**
     * <p>initServiceProviderMetadataResolver.</p>
     */
    protected void initServiceProviderMetadataResolver() {
        if (this.serviceProviderMetadataResolver == null) {
            this.serviceProviderMetadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
            this.serviceProviderMetadataResolver.resolve();
        }
    }

    protected void initIdentityProviderMetadataResolver(final boolean forceReinit) {
        if (identityProviderMetadataResolver == null) {
            this.identityProviderMetadataResolver = this.configuration.getIdentityProviderMetadataResolver();
            this.identityProviderMetadataResolver.resolve(forceReinit);
        }
    }

    protected void initDecrypter() {
        if (decrypter == null) {
            this.decrypter = new KeyStoreDecryptionProvider(configuration.getCredentialProvider()).build();
        }
    }

    /**
     * <p>initSignatureSigningParametersProvider.</p>
     */
    protected void initSignatureSigningParametersProvider() {
        if (signatureSigningParametersProvider == null) {
            this.signatureSigningParametersProvider = new DefaultSignatureSigningParametersProvider(configuration);
        }
    }

    /**
     * <p>initSAMLReplayCache.</p>
     */
    protected void initSAMLReplayCache() {
        if (replayCache == null) {
            replayCache = new InMemoryReplayCacheProvider();
        }
    }

    /**
     * <p>destroy.</p>
     */
    public void destroy() {
        ((SAML2ServiceProviderMetadataResolver) serviceProviderMetadataResolver).destroy();
    }

    /** {@inheritDoc} */
    @Override
    public void notifySessionRenewal(final CallContext ctx, final String oldSessionId) {
        val sessionLogoutHandler = findSessionLogoutHandler();
        if (sessionLogoutHandler != null) {
            sessionLogoutHandler.renewSession(ctx, oldSessionId);
        }
    }

    /**
     * <p>getIdentityProviderResolvedEntityId.</p>
     *
     * @return a {@link String} object
     */
    public final String getIdentityProviderResolvedEntityId() {
        return this.identityProviderMetadataResolver.getEntityId();
    }

    /**
     * <p>getServiceProviderResolvedEntityId.</p>
     *
     * @return a {@link String} object
     */
    public final String getServiceProviderResolvedEntityId() {
        return this.serviceProviderMetadataResolver.getEntityId();
    }

    @Override
    public void close() throws IOException {
        destroy();
    }
}
