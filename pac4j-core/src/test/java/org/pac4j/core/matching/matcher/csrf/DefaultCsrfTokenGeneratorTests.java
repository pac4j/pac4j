package org.pac4j.core.matching.matcher.csrf;

import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;

/**
 * Tests {@link DefaultCsrfTokenGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultCsrfTokenGeneratorTests {

    private final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();

    @Test
    public void test() {
        final WebContext context = MockWebContext.create();
        final String token = generator.get(context);
        Assert.assertNotNull(token);
        final String token2 = generator.get(context);
        Assert.assertEquals(token, token2);
    }
}
