package org.pac4j.saml.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
import org.pac4j.saml.crypto.DefaultSignatureSigningParametersProvider;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.KeyStoreDecryptionProvider;
import org.pac4j.saml.crypto.LogOnlySignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
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
@NoArgsConstructor
@Getter
@Setter
public class SAML2Client extends IndirectClient implements Closeable {

    protected SAMLContextProvider contextProvider;

    protected SignatureSigningParametersProvider signatureSigningParametersProvider;

    protected SAML2ResponseValidator authnResponseValidator;

    protected SAML2LogoutValidator logoutValidator;

    protected SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected SAML2MetadataResolver identityProviderMetadataResolver;

    protected SAML2MetadataResolver serviceProviderMetadataResolver;

    protected Decrypter decrypter;

    protected SAML2Configuration configuration;

    protected ValueGenerator stateGenerator;

    protected ReplayCacheProvider replayCache;

    protected SOAPPipelineProvider soapPipelineProvider;

    protected SAML2LogoutRequestMessageSender logoutRequestMessageSender;

    protected SAML2WebSSOMessageSender webSsoMessageSender;

    static {
        assertNotNull("parserPool", Configuration.getParserPool());
        assertNotNull("marshallerFactory", Configuration.getMarshallerFactory());
        assertNotNull("unmarshallerFactory", Configuration.getUnmarshallerFactory());
        assertNotNull("builderFactory", Configuration.getBuilderFactory());
    }

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

        // The order of these operations is very important
        // since they depend on each other's field references and sometimes 'this' is passed.
        initDecrypter(forceReinit);
        initSignatureSigningParametersProvider(forceReinit);
        initIdentityProviderMetadataResolver(forceReinit);
        initServiceProviderMetadataResolver(forceReinit);
        initSAMLContextProvider(forceReinit);
        initSignatureTrustEngineProvider(forceReinit);
        initSAMLReplayCache(forceReinit);
        initSAMLResponseValidator(forceReinit);
        initSOAPPipelineProvider(forceReinit);
        initSAMLLogoutResponseValidator(forceReinit);
        initRedirectActionBuilder(forceReinit);
        initCredentialExtractor(forceReinit);
        initAuthenticator(forceReinit);
        initStateGenerator(forceReinit);
        initWebSSOMessageSender(forceReinit);
        initLogoutProcessor(forceReinit);
        initLogoutRequestMessageSender(forceReinit);
        initLogoutActionBuilder(forceReinit);
    }

    protected void initStateGenerator(boolean forceReinit) {
        if (stateGenerator == null || forceReinit) {
            stateGenerator = new SAML2StateGenerator(this);
        }
    }

    protected void initWebSSOMessageSender(boolean forceReinit) {
        if (webSsoMessageSender == null || forceReinit) {
            webSsoMessageSender = new SAML2WebSSOMessageSender(
                this.signatureSigningParametersProvider,
                this.configuration.getAuthnRequestBindingType(), true,
                this.configuration.isAuthnRequestSigned());
        }
    }

    protected void initLogoutRequestMessageSender(boolean forceReinit) {
        if (logoutRequestMessageSender == null || forceReinit) {
            logoutRequestMessageSender = new SAML2LogoutRequestMessageSender(
                this.signatureSigningParametersProvider,
                this.configuration.getSpLogoutRequestBindingType(), false,
                this.configuration.isSpLogoutRequestSigned());
        }
    }

    protected void initLogoutActionBuilder(final boolean forceReinit) {
        if (isLogoutActionBuilderUndefined() || forceReinit) {
            setLogoutActionBuilder(new SAML2LogoutActionBuilder(this));
        }
    }

    protected void initRedirectActionBuilder(final boolean forceReinit) {
        if (getRedirectionActionBuilder() == null || forceReinit) {
            setRedirectionActionBuilderIfUndefined(new SAML2RedirectionActionBuilder(this));
        }
    }

    protected void initCredentialExtractor(final boolean forceReinit) {
        if (getCredentialsExtractor() == null || forceReinit) {
            setCredentialsExtractorIfUndefined(new SAML2CredentialsExtractor(this, this.identityProviderMetadataResolver,
                this.serviceProviderMetadataResolver, this.soapPipelineProvider));
        }
    }

    protected void initAuthenticator(final boolean forceReinit) {
        if (getAuthenticator() == null || forceReinit) {
            setAuthenticatorIfUndefined(new SAML2Authenticator(authnResponseValidator, this.logoutValidator,
                this.configuration.getAttributeAsId(), this.configuration.getMappedAttributes()));
        }
    }

    protected void initLogoutProcessor(final boolean forceReinit) {
        if (getLogoutProcessor() == null || forceReinit) {
            setLogoutProcessor(new SAML2LogoutProcessor(this));
        }
    }

    protected void initSOAPPipelineProvider(final boolean forceReinit) {
        if (soapPipelineProvider == null || forceReinit) {
            this.soapPipelineProvider = new DefaultSOAPPipelineProvider(this);
        }
    }


    protected void initSAMLLogoutResponseValidator(final boolean forceReinit) {
        if (logoutValidator == null || forceReinit) {
            this.logoutValidator = new SAML2LogoutValidator(this.signatureTrustEngineProvider,
                this.decrypter, findSessionLogoutHandler(),
                this.replayCache, this.configuration.getUriComparator());
            this.logoutValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
            this.logoutValidator.setPartialLogoutTreatedAsSuccess(this.configuration.isPartialLogoutTreatedAsSuccess());
        }
    }

    protected void initSAMLResponseValidator(final boolean forceReinit) {
        // Build the SAML response validator
        if (authnResponseValidator == null || forceReinit) {
            this.authnResponseValidator = new SAML2AuthnResponseValidator(
                this.signatureTrustEngineProvider,
                this.decrypter,
                this.replayCache,
                this.configuration,
                findSessionLogoutiontHandler());
            this.authnResponseValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
        }
    }

    protected void initSignatureTrustEngineProvider(final boolean forceReinit) {
        // Build provider for digital signature validation and encryption
        if (signatureTrustEngineProvider == null || forceReinit) {
            this.signatureTrustEngineProvider = new ExplicitSignatureTrustEngineProvider(
                this.identityProviderMetadataResolver, this.serviceProviderMetadataResolver);
            if (this.configuration.isAllSignatureValidationDisabled()) {
                this.signatureTrustEngineProvider = new LogOnlySignatureTrustEngineProvider(this.signatureTrustEngineProvider);
            }
        }
    }

    protected void initSAMLContextProvider(final boolean forceReinit) {
        if (this.contextProvider == null || forceReinit) {
            this.contextProvider = new SAML2ContextProvider(this.identityProviderMetadataResolver, this.serviceProviderMetadataResolver,
                this.configuration.getSamlMessageStoreFactory());
        }
    }

    protected void initServiceProviderMetadataResolver(final boolean forceReinit) {
        if (this.serviceProviderMetadataResolver == null || forceReinit) {
            this.serviceProviderMetadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
            this.serviceProviderMetadataResolver.resolve();
        }
    }

    protected void initIdentityProviderMetadataResolver(final boolean forceReinit) {
        if (identityProviderMetadataResolver == null || forceReinit) {
            this.identityProviderMetadataResolver = this.configuration.getIdentityProviderMetadataResolver();
            this.identityProviderMetadataResolver.resolve(forceReinit);
        }
    }

    protected void initDecrypter(final boolean forceReinit) {
        if (decrypter == null || forceReinit) {
            this.decrypter = new KeyStoreDecryptionProvider(configuration.getCredentialProvider()).build();
        }
    }

    protected void initSignatureSigningParametersProvider(final boolean forceReinit) {
        if (signatureSigningParametersProvider == null || forceReinit) {
            this.signatureSigningParametersProvider = new DefaultSignatureSigningParametersProvider(configuration);
        }
    }

    protected void initSAMLReplayCache(final boolean forceReinit) {
        if (replayCache == null || forceReinit) {
            replayCache = new InMemoryReplayCacheProvider();
        }
    }

    public void destroy() {
        ((SAML2ServiceProviderMetadataResolver) serviceProviderMetadataResolver).destroy();
    }

    @Override
    public void notifySessionRenewal(final CallContext ctx, final String oldSessionId) {
        val sessionLogoutHandler = findSessionLogoutHandler();
        if (sessionLogoutHandler != null) {
            sessionLogoutHandler.renewSession(ctx, oldSessionId);
        }
    }

    public final String getIdentityProviderResolvedEntityId() {
        return this.identityProviderMetadataResolver.getEntityId();
    }

    public final String getServiceProviderResolvedEntityId() {
        return this.serviceProviderMetadataResolver.getEntityId();
    }

    @Override
    public void close() {
        destroy();
    }
}
