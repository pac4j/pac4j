package org.pac4j.test.mongo;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import org.pac4j.test.util.TestsConstants;

/**
 * Simulates a MongoDB server.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class MongoServer implements TestsConstants {

    private TransitionWalker.ReachedState<RunningMongodProcess> running;

    public void start(final int port) {
        this.running = Mongod.builder()
            .net(Start.to(Net.class).initializedWith(Net.of("localhost", port, false)))
            .build()
            .start(Version.Main.V6_0);
    }

    public void stop() {
        if (running != null) {
            running.close();
        }
    }
}
