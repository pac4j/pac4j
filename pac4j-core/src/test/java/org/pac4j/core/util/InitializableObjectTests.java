package org.pac4j.core.util;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This class tests the {@link InitializableObject} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class InitializableObjectTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializableObjectTests.class);

    @Test
    public void testInitCalledOnlyOnce() {
        var io = new CustomInitializableObject(false);
        assertEquals(0, io.getCounter());
        assertEquals(0, io.getNbAttempts());
        assertNull(io.getLastAttempt());

        io.init();
        assertEquals(1, io.getCounter());
        assertEquals(1, io.getNbAttempts());
        assertNotNull(io.getLastAttempt());

        io.init();
        assertEquals(1, io.getCounter());
        assertEquals(1, io.getNbAttempts());
        assertNotNull(io.getLastAttempt());
    }

    @Test
    public void testInitCalledOnlyOnceDespiteFailuresButNotEnoughTimeBetweenRetries() {
        var io = new CustomInitializableObject(true);
        assertEquals(0, io.getCounter());
        assertEquals(0, io.getNbAttempts());
        assertNull(io.getLastAttempt());

        catchInit(io);
        assertEquals(1, io.getCounter());
        assertEquals(1, io.getNbAttempts());
        assertNotNull(io.getLastAttempt());

        catchInit(io);
        assertEquals(1, io.getCounter());
        assertEquals(1, io.getNbAttempts());
        assertNotNull(io.getLastAttempt());
    }

    private void catchInit(final InitializableObject initializableObject) {
        try {
            initializableObject.init();
        } catch (final TechnicalException e) {
            LOGGER.debug("Expected TechnicalException");
        }
    }

    @Test
    public void testInitCalledTwiceBecauseOfFailuresAndEnoughTimeBetweenRetries() {
        var io = new CustomInitializableObject(true);
        io.setMinTimeIntervalBetweenAttemptsInMilliseconds(200);
        assertEquals(0, io.getCounter());
        assertEquals(0, io.getNbAttempts());
        assertNull(io.getLastAttempt());

        catchInit(io);
        assertEquals(1, io.getCounter());
        assertEquals(1, io.getNbAttempts());
        assertNotNull(io.getLastAttempt());
        TestsHelper.wait(400);

        catchInit(io);
        assertEquals(2, io.getCounter());
        assertEquals(2, io.getNbAttempts());
        assertNotNull(io.getLastAttempt());
    }

    @Test
    public void testInitNotCalledBecauseOfMaxAttempts() {
        var io = new CustomInitializableObject(false);
        io.setMaxAttempts(0);
        assertEquals(0, io.getCounter());
        assertEquals(0, io.getNbAttempts());
        assertNull(io.getLastAttempt());

        catchInit(io);
        assertEquals(0, io.getCounter());
        assertEquals(0, io.getNbAttempts());
        assertNull(io.getLastAttempt());
    }

    private static final class CustomInitializableObject extends InitializableObject {

        private int counter;

        private boolean fails;

        public CustomInitializableObject(final boolean fails) {
            this.fails = fails;
        }

        @Override
        protected void internalInit(final boolean forceReinit) {
            this.counter++;
            if (fails) {
                throw new TechnicalException("Initialization fails");
            }
        }

        public int getCounter() {
            return this.counter;
        }
    }
}
