package org.pac4j.couch.profile.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.pac4j.couch.profile.CouchProfile;

import java.util.List;

/**
 * CouchDB profile DAO.
 *
 * @author Timur Duehr
 * @since 3.1.0
 */
public class CouchProfileRepository  extends CouchDbRepositorySupport<CouchProfile> {
    public CouchProfileRepository(CouchDbConnector couchDbConnector){
        super(CouchProfile.class, couchDbConnector);
    }

    @GenerateView
    public List<CouchProfile> findByLinkedId(final String linkedId) {
        return queryView("by_linkedId", linkedId);
    }

    @GenerateView
    public List<CouchProfile> findByUsername(final String username) {
        return queryView("by_username", username);
    }

    public List<CouchProfile> findBy(final String key, final String value ) {
        return queryView("by_" + key, value);
    }
}
