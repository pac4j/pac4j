package org.pac4j.test.cas.client.rest;

import org.pac4j.cas.client.CasClient;
import org.pac4j.test.cas.client.CasClientIT;

/**
 * The {@link CasClientRestProtocolIT} is responsible for...
 *
 * @author Misagh Moayyed
 */
public class CasClientRestProtocolIT extends CasClientIT {

    @Override
    protected CasClient.CasProtocol getCasProtocol() {
        return CasClient.CasProtocol.CAS10;
    }
}
