package org.pac4j.openid4vp.profile;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * The attributes of the EUDI person identification data.
 *
 * <p>Beware: these identifiers, the mobile document namespace and the SD-JWT VC type in particular, have
 * changed between versions of the architecture and reference framework. Check them against the version
 * actually targeted rather than against an example found online.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public class EudiPidProfileDefinition extends CommonProfileDefinition {

    /** The mobile document type and namespace of the person identification data. */
    public static final String PID_DOCTYPE = "eu.europa.ec.eudi.pid.1";

    /** The SD-JWT VC type of the person identification data. */
    public static final String PID_VCT = "urn:eudi:pid:1";

    public static final String GIVEN_NAME = "given_name";
    public static final String BIRTH_DATE = "birth_date";
    public static final String AGE_OVER_18 = "age_over_18";
    public static final String AGE_IN_YEARS = "age_in_years";
    public static final String BIRTH_PLACE = "birth_place";
    public static final String NATIONALITY = "nationality";
    public static final String RESIDENT_ADDRESS = "resident_address";
    public static final String RESIDENT_COUNTRY = "resident_country";
    public static final String RESIDENT_CITY = "resident_city";
    public static final String RESIDENT_POSTAL_CODE = "resident_postal_code";
    public static final String PERSONAL_ADMINISTRATIVE_NUMBER = "personal_administrative_number";
    public static final String ISSUING_AUTHORITY = "issuing_authority";
    public static final String ISSUING_COUNTRY = "issuing_country";
    public static final String ISSUANCE_DATE = "issuance_date";
    public static final String EXPIRY_DATE = "expiry_date";

    /**
     * <p>Constructor for EudiPidProfileDefinition.</p>
     */
    public EudiPidProfileDefinition() {
        super(x -> new VerifiableCredentialProfile());
        primary(GIVEN_NAME, Converters.STRING);
        primary(BIRTH_DATE, Converters.STRING);
        primary(AGE_OVER_18, Converters.BOOLEAN);
        primary(AGE_IN_YEARS, Converters.INTEGER);
        primary(BIRTH_PLACE, Converters.STRING);
        primary(NATIONALITY, Converters.STRING);
        primary(RESIDENT_ADDRESS, Converters.STRING);
        primary(RESIDENT_COUNTRY, Converters.STRING);
        primary(RESIDENT_CITY, Converters.STRING);
        primary(RESIDENT_POSTAL_CODE, Converters.STRING);
        primary(PERSONAL_ADMINISTRATIVE_NUMBER, Converters.STRING);
        secondary(ISSUING_AUTHORITY, Converters.STRING);
        secondary(ISSUING_COUNTRY, Converters.STRING);
        secondary(ISSUANCE_DATE, Converters.STRING);
        secondary(EXPIRY_DATE, Converters.STRING);
    }
}
