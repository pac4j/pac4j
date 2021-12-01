package org.pac4j.core.authorization.authorizer;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link RequireAnyAttributeAuthorizer}.
 *
 * @author Misagh Moayyed
 * @since 1.9.2
 */
public final class RequireAnyAttributeAuthorizerTests {

    private final MockWebContext context = MockWebContext.create();

    private List<UserProfile> profiles;

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    @Test
    public void testAttributeNotFound() {
        final var authorizer = new RequireAnyAttributeAuthorizer("");
        authorizer.setElements("name1");
        profile.addAttribute("name2", "anything-goes-here");
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testNoValueProvided() {
        final var authorizer = new RequireAnyAttributeAuthorizer("");
        authorizer.setElements("name1");
        profile.addAttribute("name1", "anything-goes-here");
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testPatternSingleValuedAttribute() {
        final var authorizer = new RequireAnyAttributeAuthorizer("^value.+");
        authorizer.setElements("name1");
        profile.addAttribute("name1", "valueAddedHere");
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testPatternFails() {
        final var authorizer = new RequireAnyAttributeAuthorizer("^v");
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2", "nothing"));
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testMatchesPattern() {
        final var authorizer = new RequireAnyAttributeAuthorizer("^v\\d");
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2", "nothing"));
        profile.addAttribute("name2", "v3");
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testMatchesEverythingByDefault() {
        final var authorizer = new RequireAnyAttributeAuthorizer();
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2"));
        profile.addAttribute("name2", "v3");
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }
}
