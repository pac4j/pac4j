package org.pac4j.core.util.security;

import lombok.val;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Security endpoint builder.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
public class SecurityEndpointBuilder {

    private static final AtomicInteger internalNumber = new AtomicInteger(1);

    public static void buildConfig(final SecurityEndpoint endpoint, Object... parameters) {
        Config config = null;
        var configProvided = false;
        for (val parameter : parameters) {
            if (parameter instanceof Config) {
                if (config != null) {
                    throw new TechnicalException("Only one Config can be used");
                } else {
                    config = (Config) parameter;
                    configProvided = true;
                }
            }
        }
        if (config == null) {
            config = new Config();
        }

        var clients = Pac4jConstants.EMPTY_STRING;
        var authorizers = Pac4jConstants.EMPTY_STRING;
        var matchers = Pac4jConstants.EMPTY_STRING;

        val paramList = new ArrayList<Object>();
        for (val parameter : parameters) {
            if (parameter instanceof Collection<?> collection) {
                collection.forEach(element -> paramList.add(element));
            } else if (parameter instanceof Object[] objects) {
                Arrays.stream(objects).forEach(element -> paramList.add(element));
            } else {
                paramList.add(parameter);
            }
        }

        int numString = 0;
        for (val parameter : paramList) {
            if (parameter instanceof String s) {
                if (!configProvided) {
                    throw new TechnicalException("Cannot accept strings without a provided Config");
                }
                if (numString == 0) {
                    clients = s;
                } else if (numString == 1) {
                    authorizers = s;
                } else if (numString == 2) {
                    matchers = s;
                } else {
                    throw new TechnicalException("Too many strings used in constructor");
                }
                numString++;
            } else if (parameter instanceof Client client) {
                val clientName = client.getName();
                val configClients = config.getClients();
                if (configClients.findClient(clientName).isEmpty()) {
                    configClients.addClient(client);
                }
                clients = addElement(clients, clientName);
            } else if (parameter instanceof Authorizer authorizer) {
                var internalName = "$int_authorizer" + internalNumber.getAndIncrement();
                config.addAuthorizer(internalName, authorizer);
                authorizers = addElement(authorizers, internalName);
            } else if (parameter instanceof Matcher matcher) {
                var internalName = "$int_matcher" + internalNumber.getAndIncrement();
                config.addMatcher(internalName, matcher);
                matchers = addElement(matchers, internalName);
            } else if (parameter instanceof HttpActionAdapter httpActionAdapter) {
                endpoint.setHttpActionAdapter(httpActionAdapter);
            } else if (parameter instanceof SecurityLogic securityLogic) {
                endpoint.setSecurityLogic(securityLogic);
            } else if (!(parameter instanceof Config)) {
                throw new TechnicalException("Unsupported parameter type: " + parameter);
            }
        }

        endpoint.setConfig(config);
        if (CommonHelper.isNotBlank(clients)) {
            endpoint.setClients(clients);
        }
        if (CommonHelper.isNotBlank(authorizers)) {
            endpoint.setAuthorizers(authorizers);
        }
        if (CommonHelper.isNotBlank(matchers)) {
            endpoint.setMatchers(matchers);
        }
    }

    private static String addElement(final String elements, final String element) {
        if (CommonHelper.isNotBlank(elements)) {
            return elements + Pac4jConstants.ELEMENT_SEPARATOR + element;
        } else {
            return element;
        }
    }
}
