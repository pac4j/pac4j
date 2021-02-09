package org.pac4j.core.config;

import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the {@link Config}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ConfigTests implements TestsConstants {

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizersSetter() {
        final var config = new Config();
        config.setAuthorizers(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizersConstructor() {
        new Config((Map<String, Authorizer >) null);
    }

    @Test
    public void testAddAuthorizer() {
        final var config = new Config();
        final var authorizer = new RequireAnyRoleAuthorizer();
        config.addAuthorizer(NAME, authorizer);
        assertEquals(authorizer, config.getAuthorizers().get(NAME));
    }

    @Test
    public void testConstructor() {
        final var client =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var config = new Config(CALLBACK_URL, client);
        assertEquals(CALLBACK_URL, config.getClients().getCallbackUrl());
        assertEquals(client, config.getClients().findAllClients().get(0));
    }
}
