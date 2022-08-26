package org.pac4j.core.util.security;

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

    public static void buildConfig(final SecurityEndpoint endpoint, final Config config, Object... parameters) {
        String clients = Pac4jConstants.EMPTY_STRING;
        String authorizers = Pac4jConstants.EMPTY_STRING;
        String matchers = Pac4jConstants.EMPTY_STRING;

        var paramList = new ArrayList<Object>();
        for (final Object parameter : parameters) {
            if (parameter instanceof Collection<?>) {
                ((Collection<?>) parameter).forEach(element -> paramList.add(element));
            } else if (parameter instanceof Object[]) {
                Arrays.stream((Object[]) parameter).forEach(element -> paramList.add(element));
            } else {
                paramList.add(parameter);
            }
        }

        int numString = 0;
        for (final Object parameter : paramList) {
            if (parameter instanceof String) {
                if (numString == 0) {
                    clients = (String) parameter;
                } else if (numString == 1) {
                    authorizers = (String) parameter;
                } else if (numString == 2) {
                    matchers = (String) parameter;
                } else {
                    throw new TechnicalException("Too many strings used in constructor");
                }
                numString++;
            } else if (parameter instanceof Client) {
                clients = addElement(clients, ((Client) parameter).getName());
            } else if (parameter instanceof Authorizer) {
                var internalName = "$int_authorizer" + internalNumber.getAndIncrement();
                config.addAuthorizer(internalName, (Authorizer) parameter);
                authorizers = addElement(authorizers, internalName);
            } else if (parameter instanceof Matcher) {
                var internalName = "$int_matcher" + internalNumber.getAndIncrement();
                config.addMatcher(internalName, (Matcher) parameter);
                matchers = addElement(matchers, internalName);
            } else if (parameter instanceof HttpActionAdapter) {
                endpoint.setHttpActionAdapter((HttpActionAdapter) parameter);
            } else if (parameter instanceof SecurityLogic) {
                endpoint.setSecurityLogic((SecurityLogic) parameter);
            } else {
                throw new TechnicalException("Unsupported parameter type: " + parameter);
            }
        }

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
