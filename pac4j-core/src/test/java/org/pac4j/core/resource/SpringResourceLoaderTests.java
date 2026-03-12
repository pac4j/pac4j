package org.pac4j.core.resource;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link SpringResourceLoader}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public class SpringResourceLoaderTests implements TestsConstants {

    @Test
    public void test() {
        val loader = new MockSpringResourceLoader();
        assertEquals(-1, loader.getLastModified());
        loader.load();
        assertEquals("0", loader.getLoaded());
        assertTrue(loader.getLastModified() > 0);
        loader.load();
        assertEquals("0", loader.getLoaded());
        assertTrue(loader.getLastModified() > 0);
        loader.load();
        assertEquals("0", loader.getLoaded());
        assertTrue(loader.getLastModified() > 0);
        assertEquals(1, loader.getSeq());
    }

    private static class MockSpringResourceLoader extends SpringResourceLoader<String> {

        private int seq = 0;

        public MockSpringResourceLoader() {
            super(new ClassPathResource("testFile.txt"));
        }

        @Override
        protected void internalLoad() {
            this.loaded = Pac4jConstants.EMPTY_STRING + seq++;
        }

        public String getLoaded() {
            return this.loaded;
        }

        public int getSeq() {
            return this.seq;
        }
    }
}
