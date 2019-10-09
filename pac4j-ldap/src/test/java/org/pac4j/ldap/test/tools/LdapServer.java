package org.pac4j.ldap.test.tools;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import org.pac4j.core.util.TestsConstants;

/**
 * Simulates a basic LDAP server.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class LdapServer implements TestsConstants {

    public final static String BASE_DN = "dc=example,dc=com";
    public final static String BASE_PEOPLE_DN = "ou=people,dc=example,dc=com";
    private final static int PORT = 33389;
    public static int port = PORT;
    public final static String CN = "cn";
    public final static String SN = "sn";
    public final static String ROLE = "role";
    public final static String ROLE1 = "role1";
    public final static String ROLE2 = "role2";

    static {
        final String sPort = System.getProperty("test.ldap.port");
        System.out.println("Reserved LDAP port: " + sPort);
        if (sPort != null) {
            port = Integer.parseInt(sPort);
        }
    }

    private InMemoryDirectoryServer ds;

    public void start() {
        try {
            final InMemoryDirectoryServerConfig dsConfig = new InMemoryDirectoryServerConfig(BASE_DN);
            dsConfig.setSchema(null);
            dsConfig.setEnforceAttributeSyntaxCompliance(false);
            dsConfig.setEnforceSingleStructuralObjectClass(false);
            dsConfig.setListenerConfigs(new InMemoryListenerConfig("myListener", null, port, null, null, null));
            dsConfig.addAdditionalBindCredentials(CN + "=" + GOOD_USERNAME + "," + BASE_PEOPLE_DN, PASSWORD);
            dsConfig.addAdditionalBindCredentials(CN + "=" + GOOD_USERNAME2 + "," + BASE_PEOPLE_DN, PASSWORD);
            this.ds = new InMemoryDirectoryServer(dsConfig);
            this.ds.add("dn: " + BASE_DN, "objectClass: organizationalUnit", "objectClass: top");
            this.ds.add("dn: " + BASE_PEOPLE_DN, "objectClass: organizationalUnit");
            this.ds.add("dn: " + CN + "=" + GOOD_USERNAME + "," + BASE_PEOPLE_DN, CN + ": " + GOOD_USERNAME, SN + ": "
                    + FIRSTNAME_VALUE, "objectClass: person");
            this.ds.add("dn: " + CN + "=" + GOOD_USERNAME2 + "," + BASE_PEOPLE_DN, ROLE + ": " + ROLE1, ROLE + ": " + ROLE2,
                    "objectClass: person");

            //Debug.setEnabled(true);

            this.ds.startListening();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        this.ds.shutDown(true);
    }
}
