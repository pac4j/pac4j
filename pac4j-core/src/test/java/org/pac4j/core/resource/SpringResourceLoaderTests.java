package org.pac4j.core.resource;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.test.util.TestsConstants;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link SpringResourceLoader}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public class SpringResourceLoaderTests implements TestsConstants {

    @Test
    public void testMultipleLoadings() {
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
    public void testForcesChangeCheckOnEveryLoad() {
        val loader = new MockSpringResourceLoader();
        loader.setForceShouldCheckForChanges(true);
        loader.load();
        loader.load();
        loader.load();
        assertEquals(3, loader.getHasChangedCallCount());
    }

    @Test
    public void testRetryDelayIsShorterBeforeFirstLoadThanAfter() throws Exception {
        // a loader that has never loaded yet uses the short retry delay
        val neverLoaded = new MockSpringResourceLoader();
        // a loader that has already loaded uses the long retry delay
        val alreadyLoaded = new MockSpringResourceLoader();
        alreadyLoaded.setForceShouldCheckForChanges(true);
        alreadyLoaded.load();
        alreadyLoaded.setForceShouldCheckForChanges(false);
        assertEquals("0", alreadyLoaded.getLoaded());

        // synchronize both change-detection clocks to "now"
        neverLoaded.hasChanged();
        alreadyLoaded.hasChanged();
        // wait long enough to exceed the short delay (2s) but stay far below the long delay (60s)
        Thread.sleep(2_500);
        assertTrue(neverLoaded.shouldCheckForChanges());
        assertFalse(alreadyLoaded.shouldCheckForChanges());
    }

    @Test
    public void testFailureAfterSuccessfulLoadKeepsLastKnownValue() {
        val loader = new MockSpringResourceLoader();
        loader.setForceShouldCheckForChanges(true);
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

    @Getter
    private static class MockSpringResourceLoader extends SpringResourceLoader<String> {

        private int seq;
        private int hasChangedCallCount;
        @Setter
        private boolean failInternalLoad;
        @Setter
        private boolean forceChanged;
        @Setter
        private boolean forceShouldCheckForChanges;

        public MockSpringResourceLoader() {
            super(new ClassPathResource("testFile.txt"));
        }

        @Override
        protected boolean shouldCheckForChanges() {
            // bypass the retry delay so load() checks for changes on every call
            return forceShouldCheckForChanges || super.shouldCheckForChanges();
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
            // simulate a changed resource so load() attempts a reload
            return forceChanged || super.hasChanged();
        }

        // 'loaded' is a protected field inherited from SpringResourceLoader, expose it for assertions
        public String getLoaded() {
            return this.loaded;
        }
    }
}
