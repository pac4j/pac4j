package org.pac4j.saml.metadata;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.shared.xml.XMLParserException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.IterableMetadataSource;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.metadata.resolver.index.impl.RoleMetadataIndex;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.resource.SpringResourceLoader;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Resolve and download idp metadata to form a metadata resolver.
 * <p>
 * The resolver supports proxies using {@link Proxy} when fetching metadata over URL resources.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
@Slf4j
public class SAML2IdentityProviderMetadataResolver extends SpringResourceLoader<MetadataResolver> implements SAML2MetadataResolver {

    @Setter
    private Proxy proxy = Proxy.NO_PROXY;
    @Setter
    private HostnameVerifier hostnameVerifier;
    @Setter
    private SSLSocketFactory sslSocketFactory;

    private final SAML2Configuration configuration;

    public SAML2IdentityProviderMetadataResolver(final SAML2Configuration configuration) {
        super(configuration.getIdentityProviderMetadataResource());
        if (configuration.getSslSocketFactory() != null) {
            setSslSocketFactory(configuration.getSslSocketFactory());
        }
        if (configuration.getHostnameVerifier() != null) {
            setHostnameVerifier(configuration.getHostnameVerifier());
        }
        this.configuration = configuration;
    }

    @Override
    public final MetadataResolver resolve(final boolean force) {
        if (force) {
            internalLoad();
        }
        return load();
    }

    protected void internalLoad() {
        val t0 = System.currentTimeMillis();
        try {
            this.loaded = initializeMetadataResolver();
        } finally {
            val t1 = System.currentTimeMillis();
            LOGGER.debug("Metadata resolution took: {} ms", t1 - t0);
        }

    }

    protected DOMMetadataResolver initializeMetadataResolver() {
        if (configuration.getIdentityProviderMetadataResource() instanceof UrlResource urlResource) {
            var fileUrl = urlResource.getURL().toString();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(fileUrl).openConnection();
                if (conn instanceof HttpsURLConnection https) {
                    if (hostnameVerifier != null) {
                        https.setHostnameVerifier(hostnameVerifier);
                    }
                    if (sslSocketFactory != null) {
                        https.setSSLSocketFactory(sslSocketFactory);
                    }
                }
                conn.setRequestMethod("HEAD");
                var supportsRange = "bytes".equalsIgnoreCase(conn.getHeaderField("Accept-Ranges"));
                if (!supportsRange) {
                    var contentLength = conn.getContentLengthLong();
                    conn.disconnect();
                    return downloadMetadata(contentLength, fileUrl);
                }
            } catch (Exception e) {
                throw new TechnicalException("Error getting idp metadata resource", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        return loadMetadataFromResource(configuration.getIdentityProviderMetadataResource());
    }

    private DOMMetadataResolver downloadMetadata(final long contentLength, final String url) {
        var numThreads = Runtime.getRuntime().availableProcessors();

        var partSize = contentLength / numThreads;
        var executor = Executors.newFixedThreadPool(numThreads);
        var destination = Files.createTempFile("idpmetadata", ".xml").toFile();
        try {
            var futures = new ArrayList<Future<Void>>();

            for (var i = 0; i < numThreads; i++) {
                var start = i * partSize;
                var end = i == numThreads - 1 ? contentLength - 1 : start + partSize - 1;

                futures.add(executor.submit(() -> {
                    var conn = (HttpURLConnection) new URL(url).openConnection();
                    if (conn instanceof HttpsURLConnection https) {
                        if (hostnameVerifier != null) {
                            https.setHostnameVerifier(hostnameVerifier);
                        }
                        if (sslSocketFactory != null) {
                            https.setSSLSocketFactory(sslSocketFactory);
                        }
                    }
                    conn.setRequestProperty("Range", "bytes=" + start + '-' + end);

                    try (var in = conn.getInputStream(); var out = new RandomAccessFile(destination, "rw")) {
                        out.seek(start);
                        var buffer = new byte[8192];
                        var bytesRead = 0;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        conn.disconnect();
                    }
                    return null;
                }));
            }

            for (var future : futures) {
                future.get();
            }
            executor.shutdown();

            var resource = new FileSystemResource(destination);
            return loadMetadataFromResource(resource);
        } catch (Exception e) {
            throw new TechnicalException("Error downloading idp metadata", e);
        } finally {
            destination.delete();
        }
    }

    private DOMMetadataResolver loadMetadataFromResource(final Resource resource) {
        try (var in = SpringResourceHelper.getResourceInputStream(
            resource,
            proxy,
            sslSocketFactory,
            hostnameVerifier,
            configuration.getIdentityProviderMetadataConnectTimeout(),
            configuration.getIdentityProviderMetadataReadTimeout()
        )) {
            var parsedInput = Configuration.getParserPool().parse(in);
            var metadataRoot = parsedInput.getDocumentElement();
            var resolver = new DOMMetadataResolver(metadataRoot);
            resolver.setIndexes(Collections.singleton(new RoleMetadataIndex()));
            resolver.setParserPool(Configuration.getParserPool());
            resolver.setFailFastInitialization(true);
            resolver.setRequireValidMetadata(true);
            resolver.setId(resolver.getClass().getCanonicalName());
            resolver.initialize();
            return resolver;
        } catch (final FileNotFoundException e) {
            throw new TechnicalException("Error loading idp metadata", e);
        } catch (final ComponentInitializationException e) {
            throw new TechnicalException("Error initializing idp metadata resolver", e);
        } catch (final XMLParserException e) {
            throw new TechnicalException("Error parsing idp metadata", e);
        } catch (final IOException e) {
            throw new TechnicalException("Error getting idp metadata resource", e);
        }
    }

    /**
     * If no idpEntityId declared, select first EntityDescriptor entityId as our IDP.
     *
     * @return entity id of the idp
     */
    protected String determineIdentityProviderEntityId() {
        var idpEntityId = configuration.getIdentityProviderEntityId();
        if (idpEntityId == null) {
            var it = ((IterableMetadataSource) loaded).iterator();
            if (it.hasNext()) {
                var entityDescriptor = it.next();
                idpEntityId = entityDescriptor.getEntityID();
            }
        }

        if (idpEntityId == null) {
            throw new SAMLException("No idp entityId found");
        }
        return idpEntityId;
    }

    @Override
    public String getEntityId() {
        val md = getEntityDescriptorElement();
        if (md instanceof EntitiesDescriptor) {
            return ((EntitiesDescriptor) md).getEntityDescriptors().get(0).getEntityID();
        }
        if (md instanceof EntityDescriptor) {
            return ((EntityDescriptor) md).getEntityID();
        }
        throw new SAMLException("No idp entityId found");
    }

    @Override
    public String getMetadata() {
        if (getEntityDescriptorElement() != null) {
            return Configuration.serializeSamlObject(getEntityDescriptorElement()).toString();
        }
        throw new TechnicalException("Metadata cannot be retrieved because entity descriptor is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final XMLObject getEntityDescriptorElement() {
        try {
            var idpEntityId = determineIdentityProviderEntityId();
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(idpEntityId)));
        } catch (final ResolverException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        }
    }
}
