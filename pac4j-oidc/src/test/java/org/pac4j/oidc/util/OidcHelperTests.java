package org.pac4j.oidc.util;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link OidcHelper}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class OidcHelperTests {

    @Test
    public void testMatchRPAlgAgainstOPAlgsKeepsRpAlgorithmWhenAvailable() {
        val opAlgs = List.of(JWSAlgorithm.HS256, JWSAlgorithm.RS256);

        val keptAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS256, opAlgs);

        assertEquals(List.of(JWSAlgorithm.RS256), keptAlgs);
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsReturnsOpAlgorithmsWhenRpAlgorithmIsUnavailable() {
        val opAlgs = List.of(JWSAlgorithm.HS256, JWSAlgorithm.RS256);

        val keptAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS512, opAlgs);

        assertSame(opAlgs, keptAlgs);
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsReturnsOpAlgorithmsWhenRpAlgorithmIsNull() {
        val opAlgs = List.of(JWSAlgorithm.HS256, JWSAlgorithm.RS256);

        val keptAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("signing", null, opAlgs);

        assertSame(opAlgs, keptAlgs);
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsThrowsOnEmptyOpAlgorithms() {
        val exception = assertThrows(TechnicalException.class,
            () -> OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS256, List.of()));

        assertEquals("There must at least one signing JWS algorithm supported on the OP side", exception.getMessage());
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsThrowsOnNullOpAlgorithms() {
        val exception = assertThrows(TechnicalException.class,
            () -> OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS256, null));

        assertEquals("opAlgs cannot be null", exception.getMessage());
    }
}
