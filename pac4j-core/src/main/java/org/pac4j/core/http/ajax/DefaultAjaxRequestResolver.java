package org.pac4j.core.http.ajax;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
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

    /** {@inheritDoc} */
    @Override
    public boolean isAjax(final CallContext ctx) {
        val webContext = ctx.webContext();
        val xmlHttpRequest = AJAX_HEADER_VALUE
            .equalsIgnoreCase(webContext.getRequestHeader(AJAX_HEADER_NAME).orElse(null));
        val hasDynamicAjaxParameter = Boolean.TRUE.toString()
            .equalsIgnoreCase(webContext.getRequestHeader(IS_AJAX_REQUEST).orElse(null));
        val hasDynamicAjaxHeader = Boolean.TRUE.toString()
            .equalsIgnoreCase(webContext.getRequestParameter(IS_AJAX_REQUEST).orElse(null));
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }

    /** {@inheritDoc} */
    @Override
    public HttpAction buildAjaxResponse(final CallContext ctx, final RedirectionActionBuilder redirectionActionBuilder) {
        String url = null;
        if (addRedirectionUrlAsHeader) {
            val action = redirectionActionBuilder.getRedirectionAction(ctx).orElse(null);
            if (action instanceof WithLocationAction) {
                url = ((WithLocationAction) action).getLocation();
            }
        }

        val webContext = ctx.webContext();
        if (!webContext.getRequestParameter(FACES_PARTIAL_AJAX_PARAMETER).isPresent()) {
            if (CommonHelper.isNotBlank(url)) {
                webContext.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
            }
            LOGGER.debug("Faces is not used: returning unauthenticated error for url: {}", url);
            return HttpActionHelper.buildUnauthenticatedAction(webContext);
        }

        val buffer = new StringBuilder();
        buffer.append("<?xml version='1.0' encoding='UTF-8'?>");
        buffer.append("<partial-response>");
        if (CommonHelper.isNotBlank(url)) {
            buffer.append("<redirect url=\"" + url.replaceAll("&", "&amp;") + "\"></redirect>");
        }
        buffer.append("</partial-response>");

        LOGGER.debug("Faces is used: returning partial response content for url: {}", url);
        return HttpActionHelper.buildFormPostContentAction(webContext, buffer.toString());
    }
}
