package org.pac4j.core.http.adapter;

import org.pac4j.core.context.JEEContext;

/**
 * No-operation HTTP action adapter for the {@link JEEContext}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class JEENopHttpActionAdapter implements HttpActionAdapter<Object, JEEContext> {

    public static final JEENopHttpActionAdapter INSTANCE = new JEENopHttpActionAdapter();

    @Override
    public Object adapt(int code, JEEContext context) {
        return null;
    }
}
