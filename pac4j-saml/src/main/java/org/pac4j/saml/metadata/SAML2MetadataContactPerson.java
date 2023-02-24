package org.pac4j.saml.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * This is {@link org.pac4j.saml.metadata.SAML2MetadataContactPerson} that allows one to specify
 * contact information in saml2 metadata generation.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class SAML2MetadataContactPerson {
    private String givenName;
    private String surname;
    private List<String> emailAddresses = new ArrayList<>();
    private List<String> telephoneNumbers = new ArrayList<>();
    private String type = "technical";
    private String companyName;

    /**
     * <p>Setter for the field <code>givenName</code>.</p>
     *
     * @param givenName a {@link java.lang.String} object
     */
    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    /**
     * <p>Setter for the field <code>surname</code>.</p>
     *
     * @param surname a {@link java.lang.String} object
     */
    public void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * <p>Setter for the field <code>emailAddresses</code>.</p>
     *
     * @param emailAddresses a {@link java.util.List} object
     */
    public void setEmailAddresses(final List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    /**
     * <p>Setter for the field <code>telephoneNumbers</code>.</p>
     *
     * @param telephoneNumbers a {@link java.util.List} object
     */
    public void setTelephoneNumbers(final List<String> telephoneNumbers) {
        this.telephoneNumbers = telephoneNumbers;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * <p>Setter for the field <code>companyName</code>.</p>
     *
     * @param companyName a {@link java.lang.String} object
     */
    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    /**
     * <p>Getter for the field <code>givenName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * <p>Getter for the field <code>surname</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSurname() {
        return surname;
    }

    /**
     * <p>Getter for the field <code>emailAddresses</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    /**
     * <p>Getter for the field <code>telephoneNumbers</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getTelephoneNumbers() {
        return telephoneNumbers;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Getter for the field <code>companyName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCompanyName() {
        return companyName;
    }
}
