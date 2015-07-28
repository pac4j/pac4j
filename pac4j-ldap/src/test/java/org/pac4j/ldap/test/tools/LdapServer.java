/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.ldap.test.tools;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;

/**
 * Simulates a basic LDAP server.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class LdapServer {

    public final static String BASE_DN = "dc=example,dc=com";
    public final static String BASE_PEOPLE_DN = "ou=people,dc=example,dc=com";
    public final static int PORT = 33389;
    public final static String CN = "cn";
    public final static String SN = "sn";
    public final static String USERNAME = "jle";
    public final static String USERNAME2 = "jleleu";
    public final static String PASSWORD = "password";
    public final static String FIRSTNAME = "Jerome";
    public final static String ROLE = "role";
    public final static String ROLE1 = "role1";
    public final static String ROLE2 = "role2";

    private InMemoryDirectoryServer ds;

    public void start() {
        try {
            final InMemoryDirectoryServerConfig dsConfig = new InMemoryDirectoryServerConfig(BASE_DN);
            dsConfig.setSchema(null);
            dsConfig.setEnforceAttributeSyntaxCompliance(false);
            dsConfig.setEnforceSingleStructuralObjectClass(false);
            dsConfig.setListenerConfigs(new InMemoryListenerConfig("myListener", null, PORT, null, null, null));
            dsConfig.addAdditionalBindCredentials(CN + "=" + USERNAME + "," + BASE_PEOPLE_DN, PASSWORD);
            dsConfig.addAdditionalBindCredentials(CN + "=" + USERNAME2 + "," + BASE_PEOPLE_DN, PASSWORD);
            this.ds = new InMemoryDirectoryServer(dsConfig);
            this.ds.add("dn: " + BASE_DN, "objectClass: organizationalUnit", "objectClass: top");
            this.ds.add("dn: " + BASE_PEOPLE_DN, "objectClass: organizationalUnit");
            this.ds.add("dn: " + CN + "=" + USERNAME + "," + BASE_PEOPLE_DN, CN + ": " + USERNAME, SN + ": "
                    + FIRSTNAME, "objectClass: person");
            this.ds.add("dn: " + CN + "=" + USERNAME2 + "," + BASE_PEOPLE_DN, ROLE + ": " + ROLE1, ROLE + ": " + ROLE2,
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
