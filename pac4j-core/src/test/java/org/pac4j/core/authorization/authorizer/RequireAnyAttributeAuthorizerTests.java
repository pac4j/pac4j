package org.pac4j.core.authorization.authorizer;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link RequireAnyAttributeAuthorizer}.
 *
 * @author Misagh Moayyed
 * @since 1.9.2
 */
public final class RequireAnyAttributeAuthorizerTests {

    private final J2EContext context = new J2EContext(mock(HttpServletRequest.class), mock(HttpServletResponse.class));

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    @Test
    public void testAttributeNotFound() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("");
        authorizer.setElements("name1");
        profile.addAttribute("name2", "anything-goes-here");
        assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testNoValueProvided() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("");
        authorizer.setElements("name1");
        profile.addAttribute("name1", "anything-goes-here");
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testPatternSingleValuedAttribute() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("^value.+");
        authorizer.setElements("name1");
        profile.addAttribute("name1", "valueAddedHere");
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testPatternFails() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("^v");
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2", "nothing"));
        assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testMatchesPattern() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("^v\\d");
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2", "nothing"));
        profile.addAttribute("name2", "v3");
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testMatchesEverythingByDefault() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer();
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2"));
        profile.addAttribute("name2", "v3");
        assertTrue(authorizer.isAuthorized(context, profiles));
    }
}
