package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * The credential formats a verifier can request and validate. The EUDI person identification data
 * is issued in both formats.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@RequiredArgsConstructor
public enum CredentialFormat {

    /** IETF SD-JWT verifiable credentials. */
    SD_JWT_VC("dc+sd-jwt"),
    /** ISO/IEC 18013-5 mobile documents. */
    MSO_MDOC("mso_mdoc");

    private final String value;

    /**
     * <p>Find the format matching the given protocol value.</p>
     *
     * @param value the value used on the wire
     * @return the format (optional)
     */
    public static Optional<CredentialFormat> from(final String value) {
        return Arrays.stream(values()).filter(format -> format.value.equals(value)).findFirst();
    }
}
