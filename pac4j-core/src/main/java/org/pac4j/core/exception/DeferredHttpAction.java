package org.pac4j.core.exception;

import org.pac4j.core.context.WebContext;

/**
 * This exception is thrown when an additional HTTP action (redirect, basic auth...) is required but you want to defer it's execution.
 * 
 * @author Garry Boyce
 * @since 1.9.1
 */
public class DeferredHttpAction extends RuntimeException {
	
	private final DeferredHttpActionCallback callback;

	/**
	 * 
	 */
	private static final long serialVersionUID = -501124832864383824L;

	public DeferredHttpAction(String message, DeferredHttpActionCallback callback) {
		super(message);
		this.callback = callback;
	}

    /**
     * Build a deferred response with message, code and callback where deferred actions can be performed.
     *
     * @param message message
     * @param callback callback where deferred actions can be performed
     * @return an HTTP response
     */
	public static DeferredHttpAction deferredHttpAction(String message, DeferredHttpActionCallback callback) {
		return new DeferredHttpAction(message, callback);
	}
	
    /**
     * Executed the deferred callback.
     *
     * @param context context
     */
	public void execute(WebContext context) {
		callback.execute(context);
	}
	
}
