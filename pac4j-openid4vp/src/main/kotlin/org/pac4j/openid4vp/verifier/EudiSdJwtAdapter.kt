package org.pac4j.openid4vp.verifier

import com.nimbusds.jose.util.JSONObjectUtils
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.JWTProcessor
import eu.europa.ec.eudi.sdjwt.NimbusSdJwtOps
import eu.europa.ec.eudi.sdjwt.vc.IssuerVerificationMethod
import eu.europa.ec.eudi.sdjwt.vc.TypeMetadataPolicy
import kotlinx.coroutines.runBlocking

/**
 * The small Kotlin boundary needed for EUDI's suspend functions returning Kotlin Result.
 * No EUDI or Kotlin type is exposed by CredentialVerifier or SdJwtVcVerifier.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
internal object EudiSdJwtAdapter {

    @JvmStatic
    fun verify(raw: String, issuerProcessor: JWTProcessor<*>): Verification = runBlocking {
        with(NimbusSdJwtOps) {
            val verifier = SdJwtVcVerifier(
                IssuerVerificationMethod.Custom(issuerProcessor.asJwtVerifier()),
                TypeMetadataPolicy.NotUsed,
                null,
            )
            val (sdJwt, holderProof) = if (raw.endsWith("~")) {
                verifier.verify(raw).getOrThrow() to null
            } else {
                val verified = verifier.verify(raw, null).getOrThrow()
                verified.sdJwt to verified.keyBindingJwt
            }
            val claims = sdJwt.recreateClaimsAndDisclosuresPerClaim().first
            Verification(JSONObjectUtils.parse(claims.toString()), holderProof)
        }
    }

    class Verification(val claims: Map<String, Any?>, val holderProof: SignedJWT?)
}
