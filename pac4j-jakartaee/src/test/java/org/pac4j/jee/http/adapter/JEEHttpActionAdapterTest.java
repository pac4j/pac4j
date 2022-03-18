package org.pac4j.jee.http.adapter;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.exception.http.StatusAction;
import org.pac4j.core.util.TestsConstants;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

/**
 * Tests the {@link JEEHttpActionAdapter}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public final class JEEHttpActionAdapterTest implements TestsConstants {

    private HttpServletResponse response;

    private JEEContext context;

    private PrintWriter writer;

    @Before
    public void setUp() throws IOException {
        response = mock(HttpServletResponse.class);
        writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        context = mock(JEEContext.class);
        when(context.getNativeResponse()).thenReturn(response);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAction() {
        JEEHttpActionAdapter.INSTANCE.adapt(null, context);
    }

    @Test
    public void testActionWithLocation() {
        JEEHttpActionAdapter.INSTANCE.adapt(new FoundAction(TestsConstants.PAC4J_URL), context);
        verify(response).setStatus(302);
        verify(context).setResponseHeader(HttpConstants.LOCATION_HEADER, TestsConstants.PAC4J_URL);
    }

    @Test
    public void testError500() throws IOException {
        JEEHttpActionAdapter.INSTANCE.adapt(new StatusAction(500), context);
        verify(response).sendError(500);
    }

    @Test
    public void testActionWithContent() {
        JEEHttpActionAdapter.INSTANCE.adapt(new OkAction(TestsConstants.VALUE), context);
        verify(response).setStatus(200);
        verify(writer).write(TestsConstants.VALUE);
    }
}
