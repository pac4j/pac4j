package org.pac4j.core.util;

import lombok.val;
import org.junit.After;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.*;

import static org.junit.Assert.*;

/**
 * Tests {@link HttpActionHelper}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public final class HttpActionHelperTest implements TestsConstants {

    @After
    public void after() {
        HttpActionHelper.setAlwaysUse401ForUnauthenticated(true);
        HttpActionHelper.setUseModernHttpCodes(true);
    }

    @Test
    public void testRedirectUrlAfterGet() {
        HttpActionHelper.setUseModernHttpCodes(true);
        val action = HttpActionHelper.buildRedirectUrlAction(MockWebContext.create(), PAC4J_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testRedirectUrlAfterPost() {
        HttpActionHelper.setUseModernHttpCodes(true);
        val action = HttpActionHelper
            .buildRedirectUrlAction(MockWebContext.create().setRequestMethod("POST"), PAC4J_URL);
        assertTrue(action instanceof SeeOtherAction);
        assertEquals(PAC4J_URL, ((SeeOtherAction) action).getLocation());
    }

    @Test
    public void testRedirectUrlAfterPostWithoutModernCode() {
        HttpActionHelper.setUseModernHttpCodes(false);
        val action = HttpActionHelper
            .buildRedirectUrlAction(MockWebContext.create().setRequestMethod("POST"), PAC4J_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testFormPostContentAction() {
        val action = HttpActionHelper.buildFormPostContentAction(MockWebContext.create(), VALUE);
        assertTrue(action instanceof OkAction);
        assertFalse(action instanceof AutomaticFormPostAction);
        assertEquals(VALUE, ((OkAction) action).getContent());
    }

    @Test
    public void testFormPostContentActionSamlRequest() {
        val content = """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="utf-8" />
                </head>
                <body onload="document.forms[0].submit()">
                    <noscript>
                        <p>
                            <strong>Note:</strong> Since your browser does not support JavaScript,
                            you must press the Continue button once to proceed.
                        </p>
                    </noscript>
                   \s
            <form action="http&#x3a;&#x2f;&#x2f;local&#x3a;8080&#x2f;cas&#x2f;idp&#x2f;profile&#x2f;SAML2&#x2f;POST&#x2f;SSO" method="post">
                        <div>
            <input type="hidden" name="RelayState" value="rs"/>               \s
            <input type="hidden" name="SAMLRequest" value="sr"/>               \s
                           \s
                        </div>
                        <noscript>
                            <div>
                                <input type="submit" value="Continue"/>
                            </div>
                        </noscript>
                    </form>
                </body>
            </html>
            """;
        val action = HttpActionHelper.buildFormPostContentAction(MockWebContext.create(), content);
        assertTrue(action instanceof AutomaticFormPostAction);
        val afpAction = (AutomaticFormPostAction) action;
        assertEquals(content, afpAction.getContent());
        assertEquals("http://local:8080/cas/idp/profile/SAML2/POST/SSO", afpAction.getUrl());
        assertEquals(2, afpAction.getData().size());
        assertEquals("sr", afpAction.getData().get("SAMLRequest"));
        assertEquals("rs", afpAction.getData().get("RelayState"));
    }

    @Test
    public void testFormPostContentActionSamlResponse() {
        val content = """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="utf-8" />
                </head>
                <body onload="document.forms[0].submit()">
                    <noscript>
                        <p>
                            <strong>Note:</strong> Since your browser does not support JavaScript,
                            you must press the Continue button once to proceed.
                        </p>
                    </noscript>
                   \s
                    <form action="http&#x3a;&#x2f;&#x2f;localhost&#x3a;8081&#x2f;callback&#x3f;client_name&#x3d;SAML2Client" method="post">
                        <div>
                           \s
                           \s
            <input type="hidden" name="SAMLResponse" value="sr"/>               \s
                        </div>
                        <noscript>
                            <div>
                                <input type="submit" value="Continue"/>
                            </div>
                        </noscript>
                    </form>
                </body>
            </html>
            """;
        val action = HttpActionHelper.buildFormPostContentAction(MockWebContext.create(), content);
        assertTrue(action instanceof AutomaticFormPostAction);
        val afpAction = (AutomaticFormPostAction) action;
        assertEquals(content, afpAction.getContent());
        assertEquals("http://localhost:8081/callback?client_name=SAML2Client", afpAction.getUrl());
        assertEquals(1, afpAction.getData().size());
        assertEquals("sr", afpAction.getData().get("SAMLResponse"));
    }

    @Test
    public void testBuildFormPostContent() {
        val content = HttpActionHelper.buildFormPostContent(MockWebContext.create().setFullRequestURL(CALLBACK_URL));
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", content);
    }

    @Test
    public void testBuildFormPostContentWithData() {
        val content = HttpActionHelper
            .buildFormPostContent(MockWebContext.create().setFullRequestURL(CALLBACK_URL).addRequestParameter(NAME, VALUE));
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input type='hidden' name=\"" + NAME + "\" value=\"" + VALUE + "\" />\n" +
            "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", content);
    }

    @Test
    public void testBuildUnauthenticated401WithHeader() {
        final WebContext context = MockWebContext.create();
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, VALUE);
        val action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertTrue(action instanceof UnauthorizedAction);
        assertEquals(VALUE, context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).get());
    }

    @Test
    public void testBuildUnauthenticated401WithoutHeader() {
        final WebContext context = MockWebContext.create();
        val action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertTrue(action instanceof UnauthorizedAction);
        assertEquals("Bearer realm=\"pac4j\"", context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).get());
    }

    @Test
    public void testBuildUnauthenticated403WithHeader() {
        HttpActionHelper.setAlwaysUse401ForUnauthenticated(false);
        final WebContext context = MockWebContext.create();
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, VALUE);
        val action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertTrue(action instanceof UnauthorizedAction);
        assertEquals(VALUE, context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).get());
    }

    @Test
    public void testBuildUnauthenticated403WithoutHeader() {
        HttpActionHelper.setAlwaysUse401ForUnauthenticated(false);
        final WebContext context = MockWebContext.create();
        val action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertTrue(action instanceof ForbiddenAction);
        assertTrue(context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).isEmpty());
    }
}
