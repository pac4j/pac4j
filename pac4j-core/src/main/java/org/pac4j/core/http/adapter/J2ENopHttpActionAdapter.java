package org.pac4j.core.http.adapter;

import org.pac4j.core.context.J2EContext;

/**
 * No-operation HTTP action adapter for the {@link J2EContext}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class J2ENopHttpActionAdapter implements HttpActionAdapter<Object, J2EContext> {

    public static final J2ENopHttpActionAdapter INSTANCE = new J2ENopHttpActionAdapter();

    @Override
    public Object adapt(int code, J2EContext context) {
        return null;
    }
}
