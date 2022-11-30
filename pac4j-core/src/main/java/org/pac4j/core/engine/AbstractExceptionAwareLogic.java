package org.pac4j.core.engine;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;

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
@Getter
@Setter
@Slf4j
public abstract class AbstractExceptionAwareLogic {

    private String errorUrl;

    /**
     * Handle exceptions.
     *
     * @param e the thrown exception
     * @param httpActionAdapter the HTTP action adapter
     * @param context the web context
     * @return the final HTTP result
     */
    protected Object handleException(final Exception e, final HttpActionAdapter httpActionAdapter, final WebContext context) {
        if (httpActionAdapter == null || context == null) {
            throw runtimeException(e);
        } else if (e instanceof HttpAction httpAction) {
            LOGGER.debug("extra HTTP action required in security: {}", httpAction.getCode());
            return httpActionAdapter.adapt(httpAction, context);
        } else {
            if (CommonHelper.isNotBlank(errorUrl)) {
                val action = HttpActionHelper.buildRedirectUrlAction(context, errorUrl);
                return httpActionAdapter.adapt(action, context);
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
        if (exception instanceof RuntimeException runtimeException) {
            throw runtimeException;
        } else {
            throw new RuntimeException(exception);
        }
    }
}
