package org.pac4j.oidc.validator.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.state.validator.SessionStoreValueRetriever;
import org.pac4j.oidc.state.validator.ValueRetriever;

import com.nimbusds.oauth2.sdk.id.State;

/**
 * General test cases for {@link SessionStoreStateValidator}.
 *
 * @author Martin Hansen
 * @since 4.0.3
 */
public final class SessionStoreStateValidatorTest implements TestsConstants {

    private final OidcClient<OidcConfiguration> client = new OidcClient<>();

    @Test
    public void testValidState() {
        final State state = new State();
        final MockWebContext context = MockWebContext.create();

        context.getSessionStore().set(context, client.getStateSessionAttributeName(), state);

        final ValueRetriever target = new SessionStoreValueRetriever();

        assertEquals(state, target.retrieve(client.getStateSessionAttributeName(), client, context).get());
    }

    public void testStateNotInSessionStore() {
        final MockWebContext context = MockWebContext.create();

        final ValueRetriever target = new SessionStoreValueRetriever();

        target.retrieve(client.getStateSessionAttributeName(), client, context).ifPresent(x -> {
            fail(x.toString());
        });
    }

    public void testStateInSessionStoreNotSame() {
        final State state = new State();
        final MockWebContext context = MockWebContext.create();

        context.getSessionStore().set(context, client.getStateSessionAttributeName(), new State());

        final ValueRetriever target = new SessionStoreValueRetriever();

        assertNotEquals(state, target.retrieve(client.getStateSessionAttributeName(), client, context).get());
    }
}
