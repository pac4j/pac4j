package org.pac4j.core.engine;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.factory.ProfileManagerFactoryAware;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Abstract logic to handle exceptions:</p>
 * <ul>
 *     <li>if it's a {@link HttpAction}, the HTTP action (which has already been performed on the web context) is "adapted"</li>
 *     <li>else if an {@link #errorUrl} is defined, the user is redirected to this error URL</li>
 *     <li>otherwise the exception is thrown again</li>
 * </ul>
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public abstract class AbstractExceptionAwareLogic<R, C extends WebContext> extends ProfileManagerFactoryAware<C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String errorUrl;

    /**
     * Handle exceptions.
     *
     * @param e the thrown exception
     * @param httpActionAdapter the HTTP action adapter
     * @param context the web context
     * @return the final HTTP result
     */
    protected R handleException(final Exception e, final HttpActionAdapter<R, C> httpActionAdapter, final C context) {
        if (httpActionAdapter == null || context == null) {
            throw runtimeException(e);
        } else if (e instanceof HttpAction) {
            final HttpAction action = (HttpAction) e;
            logger.debug("extra HTTP action required in security: {}", action.getCode());
            return httpActionAdapter.adapt(action.getCode(), context);
        } else {
            if (CommonHelper.isNotBlank(errorUrl)) {
                final HttpAction action = HttpAction.redirect(context, errorUrl);
                return httpActionAdapter.adapt(action.getCode(), context);
            } else {
                throw runtimeException(e);
            }
        }
    }

    /**
     * Wrap an Exception into a RuntimeException.
     *
     * @param exception the original exception
     * @return the RuntimeException
     */
    protected RuntimeException runtimeException(final Exception exception) {
        if (exception instanceof RuntimeException) {
            throw (RuntimeException) exception;
        } else {
            throw new RuntimeException(exception);
        }
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    /**
     * Define on which error URL the user will be redirected in case of an exception.
     *
     * @param errorUrl the error URL
     */
    public void setErrorUrl(final String errorUrl) {
        this.errorUrl = errorUrl;
    }
}
