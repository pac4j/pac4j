package org.pac4j.core.http.ajax;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;

/**
 * Default way to compute if a HTTP request is an AJAX one.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAjaxRequestResolver implements AjaxRequestResolver, HttpConstants, Pac4jConstants {

    private boolean addRedirectionUrlAsHeader = false;

    @Override
    public boolean isAjax(final WebContext context, final SessionStore sessionStore) {
        final var xmlHttpRequest = AJAX_HEADER_VALUE
            .equalsIgnoreCase(context.getRequestHeader(AJAX_HEADER_NAME).orElse(null));
        final var hasDynamicAjaxParameter = Boolean.TRUE.toString()
            .equalsIgnoreCase(context.getRequestHeader(IS_AJAX_REQUEST).orElse(null));
        final var hasDynamicAjaxHeader = Boolean.TRUE.toString()
            .equalsIgnoreCase(context.getRequestParameter(IS_AJAX_REQUEST).orElse(null));
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }

    @Override
    public HttpAction buildAjaxResponse(final WebContext context, final SessionStore sessionStore,
                                        final RedirectionActionBuilder redirectionActionBuilder) {
        String url = null;
        if (addRedirectionUrlAsHeader) {
            final var action = redirectionActionBuilder.getRedirectionAction(context, sessionStore).orElse(null);
            if (action instanceof WithLocationAction) {
                url = ((WithLocationAction) action).getLocation();
            }
        }

        if (!context.getRequestParameter(FACES_PARTIAL_AJAX_PARAMETER).isPresent()) {
            if (CommonHelper.isNotBlank(url)) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
            }
            throw HttpActionHelper.buildUnauthenticatedAction(context);
        }

        final var buffer = new StringBuilder();
        buffer.append("<?xml version='1.0' encoding='UTF-8'?>");
        buffer.append("<partial-response>");
        if (CommonHelper.isNotBlank(url)) {
            buffer.append("<redirect url=\"" + url.replaceAll("&", "&amp;") + "\"></redirect>");
        }
        buffer.append("</partial-response>");

        return HttpActionHelper.buildFormPostContentAction(context, buffer.toString());
    }

    public boolean isAddRedirectionUrlAsHeader() {
        return addRedirectionUrlAsHeader;
    }

    public void setAddRedirectionUrlAsHeader(boolean addRedirectionUrlAsHeader) {
        this.addRedirectionUrlAsHeader = addRedirectionUrlAsHeader;
    }
}
