package org.pac4j.core.exception.http;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link RedirectionActionHelper}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public final class RedirectionActionHelperTest implements TestsConstants {

    @Test
    public void testRedirectUrlAfterGet() {
        RedirectionActionHelper.setUseModernHttpCodes(true);
        final RedirectionAction action = RedirectionActionHelper.buildRedirectUrlAction(MockWebContext.create(), PAC4J_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testRedirectUrlAfterPost() {
        RedirectionActionHelper.setUseModernHttpCodes(true);
        final RedirectionAction action = RedirectionActionHelper
            .buildRedirectUrlAction(MockWebContext.create().setRequestMethod("POST"), PAC4J_URL);
        assertTrue(action instanceof SeeOtherAction);
        assertEquals(PAC4J_URL, ((SeeOtherAction) action).getLocation());
    }

    @Test
    public void testRedirectUrlAfterPostWithoutModernCode() {
        RedirectionActionHelper.setUseModernHttpCodes(false);
        final RedirectionAction action = RedirectionActionHelper
            .buildRedirectUrlAction(MockWebContext.create().setRequestMethod("POST"), PAC4J_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testFormPostContentAfterGet() {
        RedirectionActionHelper.setUseModernHttpCodes(true);
        final RedirectionAction action = RedirectionActionHelper.buildFormPostContentAction(MockWebContext.create(), VALUE);
        assertTrue(action instanceof OkAction);
        assertEquals(VALUE, ((OkAction) action).getContent());
    }

    @Test
    public void testFormPostContentAfterPost() {
        RedirectionActionHelper.setUseModernHttpCodes(true);
        final RedirectionAction action = RedirectionActionHelper
            .buildFormPostContentAction(MockWebContext.create().setRequestMethod("POST"), VALUE);
        assertTrue(action instanceof OkAction);
        assertEquals(VALUE, ((OkAction) action).getContent());
    }

    @Test
    public void testFormPostContentAfterPostWithoutModernCode() {
        RedirectionActionHelper.setUseModernHttpCodes(false);
        final RedirectionAction action = RedirectionActionHelper
            .buildFormPostContentAction(MockWebContext.create().setRequestMethod("POST"), VALUE);
        assertTrue(action instanceof OkAction);
        assertEquals(VALUE, ((OkAction) action).getContent());
    }

    @Test
    public void testBuildFormPostContent() {
        final String content = RedirectionActionHelper.buildFormPostContent(MockWebContext.create().setFullRequestURL(CALLBACK_URL));
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", content);
    }

    @Test
    public void testBuildFormPostContentWithData() {
        final String content = RedirectionActionHelper
            .buildFormPostContent(MockWebContext.create().setFullRequestURL(CALLBACK_URL).addRequestParameter(NAME, VALUE));
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input type='hidden' name=\"" + NAME + "\" value=\"" + VALUE + "\" />\n" +
            "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", content);
    }
}
