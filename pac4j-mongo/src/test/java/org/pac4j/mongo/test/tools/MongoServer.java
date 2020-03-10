package org.pac4j.mongo.test.tools;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.bson.Document;
import org.pac4j.core.credentials.password.ShiroPasswordEncoder;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.password.PasswordEncoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simulates a MongoDB server.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class MongoServer implements TestsConstants {

    public final static PasswordEncoder PASSWORD_ENCODER = new ShiroPasswordEncoder(new DefaultPasswordService());

    private MongodExecutable mongodExecutable;

    public void start(final int port) {
        final MongodStarter starter = MongodStarter.getDefaultInstance();

        try {
            final IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    .build();

            mongodExecutable = starter.prepare(mongodConfig);
            mongodExecutable.start();

            // populate
            final MongoClient mongo = new MongoClient("localhost", port);
            final MongoDatabase db = mongo.getDatabase("users");
            db.createCollection("users");
            final MongoCollection<Document> collection = db.getCollection("users");
            final String password = PASSWORD_ENCODER.encode(PASSWORD);
            final Map<String, Object> properties1 = new HashMap<>();
            properties1.put(USERNAME, GOOD_USERNAME);
            properties1.put(PASSWORD, password);
            properties1.put(FIRSTNAME, FIRSTNAME_VALUE);
            collection.insertOne(new Document(properties1));
            final Map<String, Object> properties2 = new HashMap<>();
            properties2.put(USERNAME, MULTIPLE_USERNAME);
            properties2.put(PASSWORD, password);
            collection.insertOne(new Document(properties2));
            final Map<String, Object> properties3 = new HashMap<>();
            properties3.put(USERNAME, MULTIPLE_USERNAME);
            properties3.put(PASSWORD, password);
            collection.insertOne(new Document(properties3));

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }
}
