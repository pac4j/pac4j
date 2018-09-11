package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.WritableResource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
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
import java.util.List;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ServiceProviderMetadataResolver implements SAML2MetadataResolver {
    protected static final Logger logger = LoggerFactory.getLogger(SAML2ServiceProviderMetadataResolver.class);

    private final CredentialProvider credentialProvider;
    private String spEntityId;
    private final WritableResource spMetadataResource;
    private String spMetadata;
    private final String callbackUrl;
    private final boolean forceSpMetadataGeneration;
    private final boolean authnRequestSigned;
    private final boolean wantsAssertionsSigned;
    private final String nameIdPolicyFormat;
    private final String binding;
    private final boolean signMetadata;
    private List<SAML2ServiceProvicerRequestedAttribute> requestedAttributes;

    public SAML2ServiceProviderMetadataResolver(final SAML2ClientConfiguration configuration, final String callbackUrl,
                                                final CredentialProvider credentialProvider) {
        this.authnRequestSigned = configuration.isAuthnRequestSigned();
        this.wantsAssertionsSigned = configuration.isWantsAssertionsSigned();
        this.nameIdPolicyFormat = configuration.getNameIdPolicyFormat();
        this.spMetadataResource = configuration.getServiceProviderMetadataResource();
        this.spEntityId = configuration.getServiceProviderEntityId();
        this.credentialProvider = credentialProvider;
        this.callbackUrl = callbackUrl;
        this.forceSpMetadataGeneration = configuration.isForceServiceProviderMetadataGeneration();
        this.binding = configuration.getDestinationBindingType();
        this.signMetadata = configuration.isSignMetadata();
        this.requestedAttributes = configuration.getRequestedServiceProviderAttributes();

        determineServiceProviderEntityId(callbackUrl);
        prepareServiceProviderMetadata();
    }

    private void determineServiceProviderEntityId(final String callbackUrl) {
        try {
            if (CommonHelper.isBlank(this.spEntityId)) {
                final URL url = new URL(callbackUrl);
                if (url.getQuery() != null) {
                    this.spEntityId = url.toString().replace("?" + url.getQuery(), "");
                } else {
                    this.spEntityId = url.toString();
                }
            }
            logger.info("Using SP entity ID {}", this.spEntityId);
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    private MetadataResolver prepareServiceProviderMetadata() {
        final boolean credentialProviderRequired = this.authnRequestSigned || this.wantsAssertionsSigned;
        if (credentialProviderRequired && this.credentialProvider == null) {
            throw new TechnicalException("Credentials Provider can not be null when authnRequestSigned or" +
                " wantsAssertionsSigned is set to true");
        }

        try {
            final SAML2MetadataGenerator metadataGenerator = new SAML2MetadataGenerator(binding);
            metadataGenerator.setWantAssertionSigned(this.wantsAssertionsSigned);
            metadataGenerator.setAuthnRequestSigned(this.authnRequestSigned);
            metadataGenerator.setNameIdPolicyFormat(this.nameIdPolicyFormat);
            metadataGenerator.setSignMetadata(this.signMetadata);
            metadataGenerator.setRequestedAttributes(this.requestedAttributes);
            if (credentialProviderRequired) {
                metadataGenerator.setCredentialProvider(this.credentialProvider);
            }

            metadataGenerator.setEntityId(this.spEntityId);
            metadataGenerator.setRequestInitiatorLocation(callbackUrl);
            // Assertion consumer service url is the callback url
            metadataGenerator.setAssertionConsumerServiceUrl(callbackUrl);
            // for now same for logout url
            metadataGenerator.setSingleLogoutServiceUrl(callbackUrl);

            // Initialize metadata provider for our SP and get the XML as a String
            this.spMetadata = metadataGenerator.getMetadata();
            writeServiceProviderMetadataToResource();
            return metadataGenerator.buildMetadataResolver();
        } catch (final Exception e) {
            throw new TechnicalException("Unable to generate metadata for service provider", e);
        }
    }

    private void writeServiceProviderMetadataToResource() throws IOException, TransformerException {
        if (this.spMetadataResource != null) {
            if (spMetadataResource.exists() && !this.forceSpMetadataGeneration) {
                logger.info("Metadata file already exists at {}.", this.spMetadataResource.getFile());
            } else {
                logger.info("Writing sp metadata to {}", this.spMetadataResource.getFilename());
                final File parent = spMetadataResource.getFile().getParentFile();
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
                final StreamSource source = new StreamSource(new StringReader(this.spMetadata));
                transformer.transform(source, result);
                try (final OutputStream spMetadataOutputStream = this.spMetadataResource.getOutputStream()) {
                    spMetadataOutputStream.write(result.getWriter().toString().getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    @Override
    public final MetadataResolver resolve() {
        return prepareServiceProviderMetadata();
    }

    @Override
    public final String getEntityId() {
        return this.spEntityId;
    }

    @Override
    public String getMetadata() {
        return this.spMetadata;
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
