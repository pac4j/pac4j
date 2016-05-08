package org.pac4j.core.http;

import org.pac4j.core.context.WebContext;

/**
 * No-operation HTTP action adapter.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class NopHttpActionAdapter<R extends Object> implements HttpActionAdapter<R> {

    public static final NopHttpActionAdapter INSTANCE = new NopHttpActionAdapter();

    @Override
    public R adapt(int code, WebContext context) {
        return null;
    }
}
