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
        final boolean xmlHttpRequest = AJAX_HEADER_VALUE.equalsIgnoreCase(context.getRequestHeader(AJAX_HEADER_NAME).orElse(null));
        final boolean hasDynamicAjaxParameter = Boolean.TRUE.toString()
            .equalsIgnoreCase(context.getRequestHeader(IS_AJAX_REQUEST).orElse(null));
        final boolean hasDynamicAjaxHeader = Boolean.TRUE.toString()
            .equalsIgnoreCase(context.getRequestParameter(IS_AJAX_REQUEST).orElse(null));
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }
}
