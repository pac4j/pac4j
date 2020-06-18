package org.pac4j.oidc.validator.state;

import com.nimbusds.oauth2.sdk.id.State;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.state.validator.SessionStoreStateValidator;
import org.pac4j.oidc.state.validator.StateValidator;

import static org.junit.Assert.fail;

/**
 * General test cases for {@link SessionStoreStateValidator}.
 *
 * @author Martin Hansen
 * @since  4.0.3
 */
public final class SessionStoreStateValidatorTest implements TestsConstants {

    private final OidcClient<OidcConfiguration> client = new OidcClient<>();

    @Test
    public void testValidState() {
        final State state = new State();
        final MockWebContext context = MockWebContext.create();

        context.getSessionStore().set(context, client.getStateSessionAttributeName(), state);

        final StateValidator target = new SessionStoreStateValidator();

        target.validate(state, client, context);
    }

    @Test(expected = TechnicalException.class)
    public void testNullState() {
        final State state = null;
        final MockWebContext context = MockWebContext.create();

        final StateValidator target = new SessionStoreStateValidator();

        target.validate(state, client, context);

        fail();
    }

    @Test(expected = TechnicalException.class)
    public void testStateNotInSessionStore() {
        final State state = new State();
        final MockWebContext context = MockWebContext.create();

        final StateValidator target = new SessionStoreStateValidator();

        target.validate(state, client, context);

        fail();
    }

    @Test(expected = TechnicalException.class)
    public void testStateInSessionStoreNotSame() {
        final State state = new State();
        final MockWebContext context = MockWebContext.create();

        context.getSessionStore().set(context, client.getStateSessionAttributeName(), new State());

        final StateValidator target = new SessionStoreStateValidator();

        target.validate(state, client, context);

        fail();
    }
}
