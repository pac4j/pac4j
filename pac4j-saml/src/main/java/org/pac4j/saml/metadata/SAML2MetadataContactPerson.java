package org.pac4j.saml.metadata;

import java.util.ArrayList;
import java.util.List;

public class SAML2MetadataContactPerson {
    private String givenName;
    private String surname;
    private List<String> emailAddresses = new ArrayList<>();
    private List<String> telephoneNumbers = new ArrayList<>();
    private String type = "technical";
    private String companyName;

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public void setEmailAddresses(final List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public void setTelephoneNumbers(final List<String> telephoneNumbers) {
        this.telephoneNumbers = telephoneNumbers;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public List<String> getTelephoneNumbers() {
        return telephoneNumbers;
    }

    public String getType() {
        return type;
    }

    public String getCompanyName() {
        return companyName;
    }
}
