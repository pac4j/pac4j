package org.pac4j.core.resource;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.test.util.TestsConstants;
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

    @Test
    public void testChecksChangesOnlyOnceDuringInterval() {
        val loader = new MockSpringResourceLoader();
        loader.load();
        loader.load();
        loader.load();
        assertEquals(1, loader.getHasChangedCallCount());
    }

    @Test
    public void testCanDisableDelayBetweenChecks() {
        val loader = new MockSpringResourceLoader();
        loader.setMinimumDelayBetweenChangeDetectionInMilliseconds(0);
        loader.load();
        loader.load();
        loader.load();
        assertEquals(3, loader.getHasChangedCallCount());
    }

    @Test
    public void testFailureAfterSuccessfulLoadKeepsLastKnownValue() {
        val loader = new MockSpringResourceLoader();
        loader.setMinimumDelayBetweenChangeDetectionInMilliseconds(0);
        // first load succeeds and produces a valid value
        loader.load();
        assertEquals("0", loader.getLoaded());
        // force hasChanged() to report a change so a reload is attempted on the next load()
        loader.setForceChanged(true);
        loader.setFailInternalLoad(true);
        // a valid value was previously loaded: keep serving it (graceful degradation) instead of failing hard
        loader.load();
        assertEquals("0", loader.getLoaded());
    }

    private static class MockSpringResourceLoader extends SpringResourceLoader<String> {

        private int seq = 0;
        private int hasChangedCallCount = 0;
        private boolean failInternalLoad = false;
        private boolean forceChanged = false;

        public MockSpringResourceLoader() {
            super(new ClassPathResource("testFile.txt"));
        }

        public void setFailInternalLoad(final boolean failInternalLoad) {
            this.failInternalLoad = failInternalLoad;
        }

        public void setForceChanged(final boolean forceChanged) {
            this.forceChanged = forceChanged;
        }

        @Override
        protected void internalLoad() {
            if (failInternalLoad) {
                throw new TechnicalException("simulated load failure");
            }
            this.loaded = Pac4jConstants.EMPTY_STRING + seq++;
        }

        @Override
        public boolean hasChanged() {
            hasChangedCallCount++;
            if (forceChanged) {
                // simulate a changed resource so load() attempts a reload
                return true;
            }
            return super.hasChanged();
        }

        public String getLoaded() {
            return this.loaded;
        }

        public int getSeq() {
            return this.seq;
        }

        public int getHasChangedCallCount() {
            return hasChangedCallCount;
        }
    }
}
