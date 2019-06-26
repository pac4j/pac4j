package org.pac4j.saml.sso.artifact;

import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.saml.common.SAMLObject;

public interface SOAPPipelineProvider {
    HttpClientBuilder getHttpClientBuilder();

    HttpClientMessagePipelineFactory<SAMLObject, SAMLObject> getPipelineFactory();
}
