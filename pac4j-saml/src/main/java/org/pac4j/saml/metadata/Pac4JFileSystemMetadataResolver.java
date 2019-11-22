package org.pac4j.saml.metadata;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.AbstractReloadingMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.CriterionPredicateRegistry;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.ParserPool;

public class Pac4JFileSystemMetadataResolver extends AbstractReloadingMetadataResolver implements Pac4JMetadataResolver {
    protected static final Logger logger = LoggerFactory.getLogger(Pac4JFileSystemMetadataResolver.class);

    private AbstractReloadingMetadataResolver delegateResolver;
    private Resource metadataResource;

    public Pac4JFileSystemMetadataResolver(Resource metadataResource) throws Exception {
        super();
        delegateResolver = new FilesystemMetadataResolver(metadataResource.getFile());
        this.metadataResource = metadataResource;
    }
    
    @Override
    protected void initMetadataResolver() throws ComponentInitializationException {
        delegateResolver.initialize();
    }
    
    @Override
    public String getId() {
        if (delegateResolver != null) {
            return delegateResolver.getId();
        } else {
            return super.getId();
        }
    }
    
    @Override
    public MetadataFilter getMetadataFilter() {
        return delegateResolver.getMetadataFilter();
    }

    @Override
    public boolean isRequireValidMetadata() {
        return delegateResolver.isRequireValidMetadata();
    }
    
    @Override
    public Iterable<EntityDescriptor> resolve(CriteriaSet criteriaSet) throws ResolverException {
        return delegateResolver.resolve(criteriaSet);
    }

    @Override
    public EntityDescriptor resolveSingle(CriteriaSet criteriaSet) throws ResolverException {
        return delegateResolver.resolveSingle(criteriaSet);
    }
    
    @Override
    public void setMetadataFilter(@Nullable final MetadataFilter newFilter) {
        delegateResolver.setMetadataFilter(newFilter);
    }
    
    @Override
    public void setRequireValidMetadata(final boolean requireValidMetadata) {
        delegateResolver.setRequireValidMetadata(requireValidMetadata);
    }
    
    @Override
    public DateTime getLastRefresh() {
        return delegateResolver.getLastRefresh();
    }

    @Override
    public DateTime getLastSuccessfulRefresh() {
        return delegateResolver.getLastSuccessfulRefresh();
    }

    @Override
    public DateTime getLastUpdate() {
        return delegateResolver.getLastUpdate();
    }

    @Override
    public void refresh() throws ResolverException {
        delegateResolver.refresh();
    }

    @Override
    public Boolean wasLastRefreshSuccess() {
        return delegateResolver.wasLastRefreshSuccess();
    }

    @Override
    public void forEach(Consumer<? super EntityDescriptor> arg0) {
        delegateResolver.forEach(arg0);
    }

    @Override
    public CriterionPredicateRegistry<EntityDescriptor> getCriterionPredicateRegistry() {
        return delegateResolver.getCriterionPredicateRegistry();
    }

    @Override
    public DateTime getExpirationTime() {
        return delegateResolver.getExpirationTime();
    }

    @Override
    public long getExpirationWarningThreshold() {
        return delegateResolver.getExpirationWarningThreshold();
    }

    @Override
    public Set<MetadataIndex> getIndexes() {
        return delegateResolver.getIndexes();
    }

    @Override
    public long getMaxRefreshDelay() {
        return delegateResolver.getMaxRefreshDelay();
    }

    @Override
    public long getMinRefreshDelay() {
        return delegateResolver.getMinRefreshDelay();
    }

    @Override
    public DateTime getNextRefresh() {
        return delegateResolver.getNextRefresh();
    }

    @Override
    public ParserPool getParserPool() {
        return delegateResolver.getParserPool();
    }

    @Override
    public float getRefreshDelayFactor() {
        return delegateResolver.getRefreshDelayFactor();
    }

    @Override
    public DateTime getRootValidUntil() {
        return delegateResolver.getRootValidUntil();
    }

    @Override
    public boolean isFailFastInitialization() {
        return delegateResolver.isFailFastInitialization();
    }

    @Override
    public boolean isResolveViaPredicatesOnly() {
        return delegateResolver.isResolveViaPredicatesOnly();
    }

    @Override
    public Boolean isRootValid() {
        return delegateResolver.isRootValid();
    }

    @Override
    public boolean isSatisfyAnyPredicates() {
        return delegateResolver.isSatisfyAnyPredicates();
    }

    @Override
    public boolean isUseDefaultPredicateRegistry() {
        return delegateResolver.isUseDefaultPredicateRegistry();
    }

    @Override
    public Iterator<EntityDescriptor> iterator() {
        return delegateResolver.iterator();
    }

    @Override
    public void setCriterionPredicateRegistry(CriterionPredicateRegistry<EntityDescriptor> registry) {
        delegateResolver.setCriterionPredicateRegistry(registry);
    }

    @Override
    public void setExpirationWarningThreshold(long threshold) {
        delegateResolver.setExpirationWarningThreshold(threshold);
    }

    @Override
    public void setFailFastInitialization(boolean failFast) {
        delegateResolver.setFailFastInitialization(failFast);
    }

    @Override
    public void setId(String componentId) {
        delegateResolver.setId(componentId);
    }

    @Override
    public void setIndexes(Set<MetadataIndex> newIndexes) {
        delegateResolver.setIndexes(newIndexes);
    }

    @Override
    public void setMaxRefreshDelay(long delay) {
        delegateResolver.setMaxRefreshDelay(delay);
    }

    @Override
    public void setMinRefreshDelay(long delay) {
        delegateResolver.setMinRefreshDelay(delay);
    }

    @Override
    public void setParserPool(ParserPool pool) {
        delegateResolver.setParserPool(pool);
    }

    @Override
    public void setRefreshDelayFactor(float factor) {
        delegateResolver.setRefreshDelayFactor(factor);
    }

    @Override
    public void setResolveViaPredicatesOnly(boolean flag) {
        delegateResolver.setResolveViaPredicatesOnly(flag);
    }

    @Override
    public void setSatisfyAnyPredicates(boolean flag) {
        delegateResolver.setSatisfyAnyPredicates(flag);
    }

    @Override
    public void setUseDefaultPredicateRegistry(boolean flag) {
        delegateResolver.setUseDefaultPredicateRegistry(flag);
    }

    @Override
    public Spliterator<EntityDescriptor> spliterator() {
        return delegateResolver.spliterator();
    }

    @Override
    public String toString() {
        if (delegateResolver != null) {
            return delegateResolver.toString();
        } else {
            return super.toString();
        }
    }

    @Override
    public void createParentDirectories() throws IOException {
        File resourceFile;
        try {
            resourceFile = metadataResource.getFile();
            if (resourceFile != null) {
                final File parent = resourceFile.getParentFile();
                if (parent != null) {
                    logger.info("Attempting to create directory structure for: {}", parent.getCanonicalPath());
                    if (!parent.exists() && !parent.mkdirs()) {
                        logger.warn("Could not construct the directory structure for SP metadata: {}",
                               parent.getCanonicalPath());
                    }   
                }
            }
        } catch (UnsupportedOperationException e) {
            // do nothing since likely a resource that doesn't have a filesystem
            logger.warn("no filesystem", e);
        }
    }

    @Override
    protected byte[] fetchMetadata() throws ResolverException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getMetadataIdentifier() {
        throw new UnsupportedOperationException();
    }
}
