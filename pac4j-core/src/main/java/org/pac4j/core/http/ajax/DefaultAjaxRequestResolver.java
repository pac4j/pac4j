package org.pac4j.core.http.ajax;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;

/**
 * Default way to compute if a HTTP request is an AJAX one.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAjaxRequestResolver implements AjaxRequestResolver, HttpConstants, Pac4jConstants {
    
    private boolean addRedirectionUrlAsHeader = false;
    
    @Override
    public boolean isAjax(final WebContext context) {
        final boolean xmlHttpRequest = AJAX_HEADER_VALUE.equalsIgnoreCase(context.getRequestHeader(AJAX_HEADER_NAME));
        final boolean hasDynamicAjaxParameter = Boolean.TRUE.toString().equalsIgnoreCase(context.getRequestHeader(IS_AJAX_REQUEST));
        final boolean hasDynamicAjaxHeader = Boolean.TRUE.toString().equalsIgnoreCase(context.getRequestParameter(IS_AJAX_REQUEST));
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }
    
    @Override
    public HttpAction buildAjaxResponse(WebContext context, RedirectionActionBuilder redirectionActionBuilder) {
        if (!addRedirectionUrlAsHeader) {
            if ( CommonHelper.isBlank(context.getRequestParameter(FACES_PARTIAL_AJAX_PARAMETER))) {
                throw UnauthorizedAction.INSTANCE;
            }
            return new OkAction(generateFacesPartial(null));
        } else {
            // fallback to the old behaviour
            RedirectionAction action = redirectionActionBuilder.redirect(context);
            return buildAjaxResponse(action, context);
        }
    }
    
    protected HttpAction buildAjaxResponse(final RedirectionAction action, final WebContext context) {
        if (!(action instanceof FoundAction)) {
            throw UnauthorizedAction.INSTANCE;
        }
    
        final String url = ((FoundAction) action).getLocation();

        if ( CommonHelper.isBlank(context.getRequestParameter(FACES_PARTIAL_AJAX_PARAMETER))) {
            if (CommonHelper.isNotBlank(url)) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
            }
            throw UnauthorizedAction.INSTANCE;
        }
    
        return new OkAction(generateFacesPartial(url));
    }
    
    private String generateFacesPartial(String url) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version='1.0' encoding='UTF-8'?>");
        buffer.append("<partial-response>");
        if (url != null) {
            buffer.append("<redirect url=\"" + url.replaceAll("&", "&amp;") + "\"></redirect>");
        }
        buffer.append("</partial-response>");
        return buffer.toString();
    }
    
    public boolean isAddRedirectionUrlAsHeader() {
        return addRedirectionUrlAsHeader;
    }
    
    public void setAddRedirectionUrlAsHeader(boolean addRedirectionUrlAsHeader) {
        this.addRedirectionUrlAsHeader = addRedirectionUrlAsHeader;
    }
    
}
