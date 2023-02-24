package org.pac4j.saml.sso.artifact;

import net.shibboleth.shared.httpclient.HttpClientBuilder;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;

/**
 * Provider for the components required to perform SOAP calls for
 * ArtifactResolve.
 *
 * @since 3.8.0
 * @author bidou
 */
public interface SOAPPipelineProvider {
    /**
     * <p>getHttpClientBuilder.</p>
     *
     * @return the configured builder for the http client.
     */
    HttpClientBuilder getHttpClientBuilder();

    /**
     * <p>getPipelineFactory.</p>
     *
     * @return a pipeline factory that will be used by the
     *         {@code PipelineFactoryHttpSOAPClient} to process incoming and
     *         outgoing messages.
     */
    HttpClientMessagePipelineFactory getPipelineFactory();
}
