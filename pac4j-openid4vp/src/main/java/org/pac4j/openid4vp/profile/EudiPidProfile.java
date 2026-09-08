package org.pac4j.openid4vp.profile;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.*;

/**
 * The profile built from the person identification data an EUDI wallet presented.
 *
 * <p>It gives a typed accessor to each attribute of the PID, so that an application reads
 * {@link #isAgeOver18()} rather than casting {@code getAttribute("age_over_18")}. An attribute the holder
 * did not disclose is simply null.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EudiPidProfile extends VerifiableCredentialProfile {

    @Serial
    private static final long serialVersionUID = 4152079813275538421L;

    public String getGivenName() {
        return getAttribute(GIVEN_NAME, String.class);
    }

    public String getBirthDate() {
        return getAttribute(BIRTH_DATE, String.class);
    }

    public Boolean isAgeOver18() {
        return getAttribute(AGE_OVER_18, Boolean.class);
    }

    public Integer getAgeInYears() {
        return getAttribute(AGE_IN_YEARS, Integer.class);
    }

    public String getBirthPlace() {
        return getAttribute(BIRTH_PLACE, String.class);
    }

    public String getNationality() {
        return getAttribute(NATIONALITY, String.class);
    }

    public String getResidentAddress() {
        return getAttribute(RESIDENT_ADDRESS, String.class);
    }

    public String getResidentCountry() {
        return getAttribute(RESIDENT_COUNTRY, String.class);
    }

    public String getResidentCity() {
        return getAttribute(RESIDENT_CITY, String.class);
    }

    public String getResidentPostalCode() {
        return getAttribute(RESIDENT_POSTAL_CODE, String.class);
    }

    public String getPersonalAdministrativeNumber() {
        return getAttribute(PERSONAL_ADMINISTRATIVE_NUMBER, String.class);
    }

    public String getIssuingAuthority() {
        return getAttribute(ISSUING_AUTHORITY, String.class);
    }

    public String getIssuingCountry() {
        return getAttribute(ISSUING_COUNTRY, String.class);
    }

    public String getIssuanceDate() {
        return getAttribute(ISSUANCE_DATE, String.class);
    }

    public String getExpiryDate() {
        return getAttribute(EXPIRY_DATE, String.class);
    }
}
