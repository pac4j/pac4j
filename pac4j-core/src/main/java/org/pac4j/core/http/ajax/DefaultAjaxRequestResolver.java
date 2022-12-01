package org.pac4j.core.http.ajax;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * Default way to compute if a HTTP request is an AJAX one.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Slf4j
@Getter
@Setter
public class DefaultAjaxRequestResolver implements AjaxRequestResolver, HttpConstants, Pac4jConstants {

    private boolean addRedirectionUrlAsHeader = false;

    @Override
    public boolean isAjax(final WebContext context, final SessionStore sessionStore) {
        val xmlHttpRequest = AJAX_HEADER_VALUE
            .equalsIgnoreCase(context.getRequestHeader(AJAX_HEADER_NAME).orElse(null));
        val hasDynamicAjaxParameter = Boolean.TRUE.toString()
            .equalsIgnoreCase(context.getRequestHeader(IS_AJAX_REQUEST).orElse(null));
        val hasDynamicAjaxHeader = Boolean.TRUE.toString()
            .equalsIgnoreCase(context.getRequestParameter(IS_AJAX_REQUEST).orElse(null));
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }

    @Override
    public HttpAction buildAjaxResponse(final WebContext context, final SessionStore sessionStore,
                                        final RedirectionActionBuilder redirectionActionBuilder) {
        String url = null;
        if (addRedirectionUrlAsHeader) {
            val action = redirectionActionBuilder.getRedirectionAction(context, sessionStore).orElse(null);
            if (action instanceof WithLocationAction) {
                url = ((WithLocationAction) action).getLocation();
            }
        }

        if (!context.getRequestParameter(FACES_PARTIAL_AJAX_PARAMETER).isPresent()) {
            if (CommonHelper.isNotBlank(url)) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
            }
            LOGGER.debug("Faces is not used: returning unauthenticated error for url: {}", url);
            return HttpActionHelper.buildUnauthenticatedAction(context);
        }

        val buffer = new StringBuilder();
        buffer.append("<?xml version='1.0' encoding='UTF-8'?>");
        buffer.append("<partial-response>");
        if (CommonHelper.isNotBlank(url)) {
            buffer.append("<redirect url=\"" + url.replaceAll("&", "&amp;") + "\"></redirect>");
        }
        buffer.append("</partial-response>");

        LOGGER.debug("Faces is used: returning partial response content for url: {}", url);
        return HttpActionHelper.buildFormPostContentAction(context, buffer.toString());
    }
}
