package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.apache.commons.io.FileUtils;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.WritableResource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ServiceProviderMetadataResolver implements SAML2MetadataResolver {

    protected static final Logger logger = LoggerFactory.getLogger(SAML2ServiceProviderMetadataResolver.class);

    protected final CredentialProvider credentialProvider;
    protected final String callbackUrl;
    protected final SAML2Configuration configuration;
    private String spMetadata;
    private MetadataResolver metadataResolver;

    public SAML2ServiceProviderMetadataResolver(final SAML2Configuration configuration, final String callbackUrl,
                                                final CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
        this.callbackUrl = callbackUrl;
        this.configuration = configuration;

        determineServiceProviderEntityId(callbackUrl);
    }

    public void destroy() {
        if (this.metadataResolver instanceof FilesystemMetadataResolver) {
            ((FilesystemMetadataResolver) this.metadataResolver).destroy();
            this.metadataResolver = null;
        }
    }

    private void determineServiceProviderEntityId(final String callbackUrl) {
        try {
            if (CommonHelper.isBlank(configuration.getServiceProviderEntityId())) {
                final URL url = new URL(callbackUrl);
                if (url.getQuery() != null) {
                    configuration.setServiceProviderEntityId(url.toString().replace("?" + url.getQuery(), ""));
                } else {
                    configuration.setServiceProviderEntityId(url.toString());
                }
            }
            logger.info("Using SP entity ID {}", configuration.getServiceProviderEntityId());
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    protected MetadataResolver prepareServiceProviderMetadata() {
        try {
            final SAMLMetadataGenerator metadataGenerator = buildMetadataGenerator();

            // Initialize metadata provider for our SP and get the XML as a String
            final EntityDescriptor entity = metadataGenerator.buildEntityDescriptor();
            final String tempMetadata = metadataGenerator.getMetadata(entity);
            this.spMetadata = tempMetadata;
            writeServiceProviderMetadataToResource(tempMetadata);
            return metadataGenerator.buildMetadataResolver(configuration.getServiceProviderMetadataResource());
        } catch (final Exception e) {
            throw new TechnicalException("Unable to generate metadata for service provider", e);
        }
    }

    protected SAMLMetadataGenerator buildMetadataGenerator() {
        final SAML2MetadataGenerator metadataGenerator = new SAML2MetadataGenerator();
        fillSAML2MetadataGenerator(metadataGenerator);
        return metadataGenerator;
    }

    protected void fillSAML2MetadataGenerator(final SAML2MetadataGenerator metadataGenerator) {
        metadataGenerator.setWantAssertionSigned(configuration.isWantsAssertionsSigned());
        metadataGenerator.setAuthnRequestSigned(configuration.isAuthnRequestSigned());
        metadataGenerator.setSignMetadata(configuration.isSignMetadata());
        metadataGenerator.setNameIdPolicyFormat(configuration.getNameIdPolicyFormat());
        metadataGenerator.setRequestedAttributes(configuration.getRequestedServiceProviderAttributes());

        metadataGenerator.setCredentialProvider(this.credentialProvider);

        metadataGenerator.setEntityId(configuration.getServiceProviderEntityId());
        metadataGenerator.setRequestInitiatorLocation(callbackUrl);
        // Assertion consumer service url is the callback URL
        metadataGenerator.setAssertionConsumerServiceUrl(callbackUrl);
        metadataGenerator.setResponseBindingType(configuration.getResponseBindingType());
        final String logoutUrl = CommonHelper.addParameter(callbackUrl, Pac4jConstants.LOGOUT_ENDPOINT_PARAMETER, "true");
        // the logout URL is callback URL with an extra parameter
        metadataGenerator.setSingleLogoutServiceUrl(logoutUrl);

        // Algorithm support
        metadataGenerator.setBlackListedSignatureSigningAlgorithms(
            new ArrayList<>(configuration.getBlackListedSignatureSigningAlgorithms())
        );
        metadataGenerator.setSignatureAlgorithms(configuration.getSignatureAlgorithms());
        metadataGenerator.setSignatureReferenceDigestMethods(configuration.getSignatureReferenceDigestMethods());

        metadataGenerator.setSupportedProtocols(configuration.getSupportedProtocols());
        metadataGenerator.setContactPersons(configuration.getContactPersons());
        metadataGenerator.setMetadataUIInfos(configuration.getMetadataUIInfos());
    }

    private void writeServiceProviderMetadataToResource(final String tempMetadata) throws Exception {
        final WritableResource metadataResource = configuration.getServiceProviderMetadataResource();
        if (metadataResource != null && CommonHelper.isNotBlank(tempMetadata)) {
            if (metadataResource.exists() && !configuration.isForceServiceProviderMetadataGeneration()) {
                logger.info("Metadata file already exists at {}.", metadataResource.getFile());
            } else {
                logger.info("Writing sp metadata to {}", metadataResource.getFilename());
                final File parent = metadataResource.getFile().getParentFile();
                if (parent != null) {
                    logger.info("Attempting to create directory structure for: {}", parent.getCanonicalPath());
                    if (!parent.exists() && !parent.mkdirs()) {
                        logger.warn("Could not construct the directory structure for SP metadata: {}",
                            parent.getCanonicalPath());
                    }
                }
                final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                final StreamResult result = new StreamResult(new StringWriter());
                final StreamSource source = new StreamSource(new StringReader(tempMetadata));
                transformer.transform(source, result);
                try (OutputStream spMetadataOutputStream = metadataResource.getOutputStream()) {
                    spMetadataOutputStream.write(result.getWriter().toString().getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    @Override
    public final MetadataResolver resolve() {
        if (this.metadataResolver == null) {
            this.metadataResolver = prepareServiceProviderMetadata();
        }
        return this.metadataResolver;
    }

    @Override
    public final String getEntityId() {
        return configuration.getServiceProviderEntityId();
    }

    @Override
    public String getMetadata() throws IOException {
        if (configuration.getServiceProviderMetadataResource() != null) {
            return FileUtils.readFileToString(configuration.getServiceProviderMetadataResource().getFile(), StandardCharsets.UTF_8);
        } else if (this.spMetadata != null) {
            return this.spMetadata;
        }
        return null;

    }

    @Override
    public XMLObject getEntityDescriptorElement() {
        try {
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(getEntityId())));
        } catch (final ResolverException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        }
    }
}
