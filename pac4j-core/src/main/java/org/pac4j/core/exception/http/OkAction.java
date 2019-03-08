package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

import java.util.Map;

/**
 * An OK HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class OkAction extends RedirectionAction implements WithContentAction {

    private final String content;

    public OkAction(final String content) {
        super(HttpConstants.OK);
        this.content = content;
    }

    public static OkAction buildFormContentFromUrlAndData(final String url, final Map<String, String[]> parameters) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<html>\n");
        buffer.append("<body>\n");
        buffer.append("<form action=\"" + escapeHtml(url) + "\" name=\"f\" method=\"post\">\n");
        if (parameters != null) {
            for (final Map.Entry<String, String[]> entry : parameters.entrySet()) {
                final String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    buffer.append("<input type='hidden' name=\"" + escapeHtml(entry.getKey()) + "\" value=\"" + values[0] + "\" />\n");
                }
            }
        }
        buffer.append("<input value='POST' type='submit' />\n");
        buffer.append("</form>\n");
        buffer.append("<script type='text/javascript'>document.forms['f'].submit();</script>\n");
        buffer.append("</body>\n");
        buffer.append("</html>\n");
        return new OkAction(buffer.toString());
    }

    protected static String escapeHtml(final String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
    }

    @Override
    public String getContent() {
        return content;
    }
}
