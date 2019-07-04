package org.pac4j.saml.sso.artifact;

import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.saml.common.SAMLObject;

/**
 * Provider for the components required to perform SOAP calls for
 * ArtifactResolve.
 * 
 * @since 3.8.0
 */
public interface SOAPPipelineProvider {
    /**
     * @return the configured builder for the http client.
     */
    HttpClientBuilder getHttpClientBuilder();

    /**
     * @return a pipeline factory that will be used by the
     *         {@code PipelineFactoryHttpSOAPClient} to process incoming and
     *         outgoing messages.
     */
    HttpClientMessagePipelineFactory<SAMLObject, SAMLObject> getPipelineFactory();
}
