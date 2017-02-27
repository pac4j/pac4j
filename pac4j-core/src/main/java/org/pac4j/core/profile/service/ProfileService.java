package org.pac4j.core.profile.service;

import org.pac4j.core.profile.CommonProfile;

/**
 * Profile services: creation, update, delete and retrievals in the storage.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public interface ProfileService<U extends CommonProfile> {

    /**
     * Create a profile with the associated password in the storage.
     *
     * @param profile the profile
     * @param password the password
     */
    void create(U profile, String password);

    /**
     * Update a profile (with the associated password) in the storage.
     *
     * @param profile the profile
     * @param password the optional password
     */
    void update(U profile, String password);

    /**
     * Rmove a profile in the storage.
     *
     * @param profile the profile
     */
    void remove(U profile);

    /**
     * Remove a profile by its identifier in the storage.
     *
     * @param id the profile identifier
     */
    void removeById(String id);

    /**
     * Find a profile by its identifier.
     *
     * @param id the identifier
     * @return the found profile
     */
    U findById(String id);

    /**
     * Find a profile by its linked identifier.
     *
     * @param linkedId the linked identifier
     * @return the found profile
     */
    U findByLinkedId(String linkedId);
}
