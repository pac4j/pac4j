package org.pac4j.core.exception.http;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link OkAction}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class OkActionTest implements TestsConstants {

    @Test
    public void testPost() {
        final OkAction action = OkAction.buildFormContentFromUrlAndData(CALLBACK_URL, null);
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", action.getContent());
    }

    @Test
    public void testPostWithData() {
        final Map<String, String[]> map = new HashMap<>();
        map.put(NAME, new String[] {VALUE});
        final OkAction action = OkAction.buildFormContentFromUrlAndData(CALLBACK_URL, map);
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input type='hidden' name=\"" + NAME + "\" value=\"" + VALUE + "\" />\n" +
            "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", action.getContent());
    }
}
