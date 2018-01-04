package org.pac4j.core.http.ajax;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;

/**
 * Default way to compute if a HTTP request is an AJAX one.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAjaxRequestResolver implements AjaxRequestResolver, HttpConstants, Pac4jConstants {

    @Override
    public boolean isAjax(final WebContext context) {
        final boolean xmlHttpRequest = context.getRequestHeader(AJAX_HEADER_NAME)
            .map(h -> AJAX_HEADER_VALUE.equalsIgnoreCase(h))
            .orElse(false);
        final boolean hasDynamicAjaxParameter = context.getRequestHeader(IS_AJAX_REQUEST)
            .map(dap -> Boolean.TRUE.toString().equalsIgnoreCase(dap))
            .orElse(false);
        final boolean hasDynamicAjaxHeader = context.getRequestParameter(IS_AJAX_REQUEST)
            .map(h -> Boolean.TRUE.toString().equalsIgnoreCase(h))
            .orElse(false);
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }
}
