package org.pac4j.core.redirect;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link RedirectAction}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class RedirectActionTests implements TestsConstants {

    @Test
    public void testPost() {
        final String s = RedirectAction.post(CALLBACK_URL, null).getContent();
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
                + "<input value='POST' type='submit' />\n</form>\n" +
                "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
                "</body>\n</html>\n", s);
    }

    @Test
    public void testPostWithData() {
        final Map<String, String> map = new HashMap<>();
        map.put(NAME, VALUE);
        final String s = RedirectAction.post(CALLBACK_URL, map).getContent();
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
                + "<input type='hidden' name=\"" + NAME + "\" value=\"" + VALUE + "\" />\n" +
                "<input value='POST' type='submit' />\n</form>\n" +
                "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
                "</body>\n</html>\n", s);
    }
}
