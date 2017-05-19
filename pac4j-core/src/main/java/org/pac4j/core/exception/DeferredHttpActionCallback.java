package org.pac4j.core.exception;

import org.pac4j.core.context.WebContext;

/**
 * This is a callback where a future HTTP action can be performed.
 * 
 * @author Garry Boyce
 * @since 1.9.1
 */
public interface DeferredHttpActionCallback {

    /**
     * Executed the deferred callback.
     *
     * @param context context
     */
	void execute(WebContext context);

}
