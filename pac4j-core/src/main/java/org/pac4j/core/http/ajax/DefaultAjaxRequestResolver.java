package org.pac4j.core.http.ajax;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.exception.http.TemporaryRedirectAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.util.CommonHelper;

/**
 * Default way to compute if a HTTP request is an AJAX one.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAjaxRequestResolver implements AjaxRequestResolver, HttpConstants, Pac4jConstants {

    @Override
    public boolean isAjax(final WebContext context) {
        final boolean xmlHttpRequest = AJAX_HEADER_VALUE.equalsIgnoreCase(context.getRequestHeader(AJAX_HEADER_NAME));
        final boolean hasDynamicAjaxParameter = Boolean.TRUE.toString().equalsIgnoreCase(context.getRequestHeader(IS_AJAX_REQUEST));
        final boolean hasDynamicAjaxHeader = Boolean.TRUE.toString().equalsIgnoreCase(context.getRequestParameter(IS_AJAX_REQUEST));
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }

    @Override
    public HttpAction buildAjaxResponse(final HttpAction action, final WebContext context) {
        if (!(action instanceof TemporaryRedirectAction)) {
            throw UnauthorizedAction.INSTANCE;
        }

        final String url = ((TemporaryRedirectAction) action).getLocation();

        if ( CommonHelper.isBlank(context.getRequestParameter(FACES_PARTIAL_AJAX_PARAMETER))) {
            if (CommonHelper.isNotBlank(url)) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
            }
            throw UnauthorizedAction.INSTANCE;
        }

        final StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version='1.0' encoding='UTF-8'?>");
        buffer.append("<partial-response>");
        buffer.append("<redirect url=\"" + url.replaceAll("&", "&amp;") + "\"></redirect>");
        buffer.append("</partial-response>");

        return new OkAction(buffer.toString());
    }
}
