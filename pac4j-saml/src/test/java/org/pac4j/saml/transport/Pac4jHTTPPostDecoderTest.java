package org.pac4j.saml.transport;

import static org.junit.Assert.assertTrue;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.saml2.core.impl.ResponseImpl;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.saml.util.Configuration;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

/**
 * Tests {@link Pac4jHTTPPostDecoder}.
 *
 * @author Garry Boyce
 * @since 4.0.0
 */
public class Pac4jHTTPPostDecoderTest {
    
    private static final String  SAML_RESPONSE = "PHNhbWxwOlJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6"
            + "%0D%0AcHJvdG9jb2wiIElEPSJzMjc0ZmUwOWVkMTRkOTYwZjk4MzkwODgwOTIyOTU4NzRiOTJmZmQ1Yjgi%0D%0AIEluUmVzcG9uc2VUbz0iX3Rq"
            + "OTN6bnE2ZWc5d2c2Y29nb3hhcWplOHJucGI5bWlnd2pmeXRweiIg%0D%0AVmVyc2lvbj0iMi4wIiBJc3N1ZUluc3RhbnQ9IjIwMTktMTItMDRUMDA"
            + "6MDA6MTVaIiBEZXN0aW5h%0D%0AdGlvbj0iaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0My9hbnpvX2F1dGhlbnRpY2F0ZT9jbGllbnRfbmFt%0D%0AZT1HU0FNTCI"
            + "%2BPHNhbWw6SXNzdWVyIHhtbG5zOnNhbWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1M%0D%0AOjIuMDphc3NlcnRpb24iPmh0dHBzOi8vaWRwLnNzb2NpcmNs"
            + "ZS5jb208L3NhbWw6SXNzdWVyPjxz%0D%0AYW1scDpTdGF0dXMgeG1sbnM6c2FtbHA9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpwcm90%0D"
            + "%0Ab2NvbCI%2BCjxzYW1scDpTdGF0dXNDb2RlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6%0D%0AU0FNTDoyLjA6cHJvdG9jb2wiIFZ"
            + "hbHVlPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6c3Rh%0D%0AdHVzOlN1Y2Nlc3MiPgo8L3NhbWxwOlN0YXR1c0NvZGU%2BCjwvc2FtbHA6U3R"
            + "hdHVzPjxzYW1sOkFz%0D%0Ac2VydGlvbiB4bWxuczpzYW1sPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9u%0D%0AIiBJRD0iczIz"
            + "YmEwOGVhMjE3YzYxOWFhOGQzNDM0MjRhZjllZjdkMDFiMzBmMWIwIiBJc3N1ZUlu%0D%0Ac3RhbnQ9IjIwMTktMTItMDRUMDA6MDA6MTVaIiBWZXJzaW9u"
            + "PSIyLjAiPgo8c2FtbDpJc3N1ZXI%2B%0D%0AaHR0cHM6Ly9pZHAuc3NvY2lyY2xlLmNvbTwvc2FtbDpJc3N1ZXI%2BPGRzOlNpZ25hdHVyZSB4bWxu%0D"
            + "%0Aczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI%2BCjxkczpTaWduZWRJbmZv%0D%0APgo8ZHM6Q2Fub25pY2FsaXphdGlvbk1l"
            + "dGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3Jn%0D%0ALzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPgo8ZHM6U2lnbmF0dXJlTWV0aG9kIEFsZ29yaX"
            + "RobT0i%0D%0AaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGExIi8%2BCjxkczpSZWZlcmVu%0D%0AY2UgVVJJPSIjczIzYmEwOGVhMj"
            + "E3YzYxOWFhOGQzNDM0MjRhZjllZjdkMDFiMzBmMWIwIj4KPGRz%0D%0AOlRyYW5zZm9ybXM%2BCjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3L"
            + "nczLm9yZy8y%0D%0AMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPgo8ZHM6VHJhbnNmb3JtIEFsZ29y%0D%0AaXRobT0iaHR0cDovL"
            + "3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIi8%2BCjwvZHM6VHJh%0D%0AbnNmb3Jtcz4KPGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Im"
            + "h0dHA6Ly93d3cudzMub3JnLzIw%0D%0AMDAvMDkveG1sZHNpZyNzaGExIi8%2BCjxkczpEaWdlc3RWYWx1ZT5UQVJoRHQ1U1VIeTBFUUE2Y3Nx%0D"
            + "%0AcWx4NHpJWnM9PC9kczpEaWdlc3RWYWx1ZT4KPC9kczpSZWZlcmVuY2U%2BCjwvZHM6U2lnbmVkSW5m%0D%0Abz4KPGRzOlNpZ25hdHVyZVZhbHVlPgpH"
            + "MTFUbWVCVzF6Vzk1eVQ5NC9DcmZWVTB2SXYyR21vUkNP%0D%0AVUFQUVdleDZFamwzUDZrTTlkY2lvb1oxQnRqcHJSVUN6Z2orYVQ2VWw5CmJoa2RndFY5T"
            + "U8vdFRa%0D%0AcVR2aVJHMkNTQ2NyL0ZOSFVnNVlDaFpHVmFTUmFQditmK3Z4L05xZmZvOC85RDd5MWZFajZ6VHdO%0D%0ARmEzZ28KZU5NaE9MZlN1THFI"
            + "UWdjdDUvVDQvclFkOFdOT21PYnhacEZBdFJ6UGtkTjZ0TWxFclph%0D%0AdUR4c0VmdUcvZlI1MzUzZkxrWHFZQ2djdgpoT1pWMHpzNGZYRnFuWmtTNFFtb"
            + "klodVZUTlhJUlNC%0D%0AVm1rdEJEcG4xMzNJQStsK3ZsQ2pvbm9FdGx3VGEzaE0ycEtkVk9GWDhQQ3hICnhMVTJjck81Q2RF%0D%0AYnNBMlJTajc1NGNpW"
            + "HFqOWlmNDJZdFR4MGdBPT0KPC9kczpTaWduYXR1cmVWYWx1ZT4KPGRzOktl%0D%0AeUluZm8%2BCjxkczpYNTA5RGF0YT4KPGRzOlg1MDlDZXJ0aWZpY2F0Z"
            + "T4KTUlJRVl6Q0NBa3VnQXdJ%0D%0AQkFnSURJQVptTUEwR0NTcUdTSWIzRFFFQkN3VUFNQzR4Q3pBSkJnTlZCQVlUQWtSRk1SSXdFQVlE%0D%0AVlFR"
            + "SwpEQWxUVTA5RGFYSmpiR1V4Q3pBSkJnTlZCQU1NQWtOQk1CNFhEVEUyTURnd016RTFNRE15%0D%0ATTFvWERUSTJNRE13TkRFMU1ETXlNMW93ClBURUxN"
            + "QWtHQTFVRUJoTUNSRVV4RWpBUUJnTlZCQW9U%0D%0AQ1ZOVFQwTnBjbU5zWlRFYU1CZ0dBMVVFQXhNUmFXUndMbk56YjJOcGNtTnMKWlM1amIyMHdnZ0Vp"
            + "%0D%0ATUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFDQXdXSnlPWWhZbVdaRjJUSnZt%0D%0AMVZ5WmNjcwozWkowVHNOY29henIycF"
            + "RXY1k4V1RSYklWOWQwNnpZam5ndldpYnlpeWxld0dYY1lP%0D%0ATkIxMDZaTlVkTmdybUZkNTE5NFdzeXg2YlB2Cm5qWkVFUm55OUxPZnV3UWFxRFllS2hJ"
            + "NmMrdmVY%0D%0AQXBuT2ZzWTI2dTlMcWI5c2dhOUpuQ2tVR1Jhb1ZyQVZNM3lmZ2h2L0NnL1FFZysKSTZTVkVTNzV0%0D%0AS2RjTERUdC9Gd21BWUR"
            + "FQlY4bDUyYmNNRE5GK0pXdEF1ZXRJOS9kV0NCZTlWVENhc0FyMkZ4dzFa%0D%0AWVRBaXFHSTlzVwo0a1dTMkFwZWRicXNnSDNxcU1sUEE3dGc5aUt5O"
            + "Fl3L2RlRW4wcVFJeDhHbFZu%0D%0AUUZwRGd6RzlrK2p3Qm9lYkFZZkd2TWNPL0JEWEQyCnBiV1ROK0R2YlVSbEFnTUJBQUdqZXpCNU1B%0D%0Aa0dBM"
            + "VVkRXdRQ01BQXdMQVlKWUlaSUFZYjRRZ0VOQkI4V0hVOXdaVzVUVTB3Z1IyVnUKWlhKaGRH%0D%0AVmtJRU5sY25ScFptbGpZWFJsTUIwR0ExVWREZ1FX"
            + "QkJRaEFtQ2V3RTdhb25BdnlKZmpJbUNSWkR0%0D%0AY2NUQWZCZ05WSFNNRQpHREFXZ0JUQTFuRUErMHphNnBwTEl0a09YNXlFcDhjUWFUQU5CZ2txaGtp"
            + "%0D%0ARzl3MEJBUXNGQUFPQ0FnRUFBaEM1L1dzRjl6dEpIZ28rCng5S1Y5YnFWUzBNbXNncEcyNnlPQXFG%0D%0AWXdPU1BtVXVZbUptSGdtS0dqS3JqMW"
            + "ZkQ0lOdHpjQkhGRkJDMW1hR0ozM2xNazJiTTJUSHgKMjIv%0D%0ATzkzZjRSRm5GYWI3dDIzalJGY0YwYW1RVU9zRHZsdGZKdzdYQ2FsOEpkZ1BVZzZUTkM"
            + "0Rnk5WFl2%0D%0AME9BSGMzb0RwM3ZsMVlqOAovMXFCZzZSYzM5a2VobUQ1djhTS1ltcEU3eUZLeERGMW9sOURLREcv%0D%0ATHZDbFN2bnVWUDBiNE"
            + "JXZEJBQTlhSlNGdGROR2dFdnBFVXFHCmtKMW9zTFZxQ012U1lzVXRIbWFw%0D%0AYVgzaGlNOVJiWDM4anNTZ3NsNDRSYXI1SW9jN0tYT09aRkdmRU"
            + "t5eVVxdWNZcGpXQ09YSkVMQVYK%0D%0AQXpwN1hUdkEycTU1dTMxaE8wdzhZeDR1RVFLbG14RHVabXhwTXo0RVdBUnlqSFNBdURLRVcxUkp2%0D%0AVX"
            + "I2KzV1QTlxZU9LeExpS04xagpvNmVXQWNsNldyOU1yZVhSOWtGcFM2a0hsbGZkVlNySkVTNFNU%0D%0AMHVoMUpwNEVZZ21peU1tRkNiVXBLWGlmcHNOV"
            + "0NMRGVuRTNobGxGCjArcTN3SWR1KzRQODJSSU03%0D%0AMW43cVZnbkRuSzI5d25MaEhEYXQ5cmtDNjJDSWJvbnBrVlltblJlWDBqemUrN3R3UmFuSk9NQ"
            + "0or%0D%0AbEYKZzE2QkR2QmNHOHUwbi93SURrSEhpdEJJN2JVMWs2YzZEeWRMUSs2OWg4U0NvNnNPOVl1RCsv%0D%0AM3hBR0thZDRJbVo2dlR3bEI0ek"
            + "RDcAp1NllnUVdvY1dSWEUrVmtPYitSQmZ2UDc1NVBVYUxmTDYz%0D%0AQUZWbHBPbkVwSWlvNSsrVWpOSlJ1UHVBQT0KPC9kczpYNTA5Q2VydGl"
            + "maWNhdGU%2BCjwvZHM6WDUw%0D%0AOURhdGE%2BCjwvZHM6S2V5SW5mbz4KPC9kczpTaWduYXR1cmU%2BPHNhbWw6U3ViamVjdD4KPHNhbWw6%0D"
            + "%0ATmFtZUlEIEZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6%0D%0AdHJhbnNpZW50IiBOYW1lUXVhbGlm"
            + "aWVyPSJodHRwczovL2lkcC5zc29jaXJjbGUuY29tIj44RXA5%0D%0ANjNJcWpBT3pHQWo0WkdpaGdhMjNxNHdDPC9zYW1sOk5hbWVJRD48c2FtbDpTd"
            + "WJqZWN0Q29uZmly%0D%0AbWF0aW9uIE1ldGhvZD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmNtOmJlYXJlciI%2BCjxz%0D%0AYW1sOlN1Ympl"
            + "Y3RDb25maXJtYXRpb25EYXRhIEluUmVzcG9uc2VUbz0iX3RqOTN6bnE2ZWc5d2c2%0D%0AY29nb3hhcWplOHJucGI5bWlnd2pmeXRweiIgTm90T25PckF"
            + "mdGVyPSIyMDE5LTEyLTA0VDAwOjEw%0D%0AOjE1WiIgUmVjaXBpZW50PSJodHRwczovL2xvY2FsaG9zdDo4NDQzL2Fuem9fYXV0aGVudGljYXRl%0D%0AP2"
            + "NsaWVudF9uYW1lPUdTQU1MIi8%2BPC9zYW1sOlN1YmplY3RDb25maXJtYXRpb24%2BCjwvc2FtbDpT%0D%0AdWJqZWN0PjxzYW1sOkNvbmRpdGlvbnMgTm90"
            + "QmVmb3JlPSIyMDE5LTEyLTAzVDIzOjUwOjE1WiIg%0D%0ATm90T25PckFmdGVyPSIyMDE5LTEyLTA0VDAwOjEwOjE1WiI%2BCjxzYW1sOkF1ZGllbmNlUmV"
            + "zdHJp%0D%0AY3Rpb24%2BCjxzYW1sOkF1ZGllbmNlPmh0dHBzOi8vbG9jYWxob3N0Ojg0NDMvYW56b19hdXRoZW50%0D%0AaWNhdGU8L3NhbWw6QXVkaWV"
            + "uY2U%2BCjwvc2FtbDpBdWRpZW5jZVJlc3RyaWN0aW9uPgo8L3NhbWw6%0D%0AQ29uZGl0aW9ucz4KPHNhbWw6QXV0aG5TdGF0ZW1lbnQgQXV0aG5JbnN0"
            + "YW50PSIyMDE5LTEyLTAz%0D%0AVDIzOjU5OjQzWiIgU2Vzc2lvbkluZGV4PSJzMmYxODJmNjkzNDU0ZDA1YWI4Yzc1ZDhiYjE3MTA0%0D%0ANTczMGFjNzJ"
            + "kMDEiPjxzYW1sOkF1dGhuQ29udGV4dD48c2FtbDpBdXRobkNvbnRleHRDbGFzc1Jl%0D%0AZj51cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YWM6Y2xhc"
            + "3NlczpQYXNzd29yZFByb3RlY3Rl%0D%0AZFRyYW5zcG9ydDwvc2FtbDpBdXRobkNvbnRleHRDbGFzc1JlZj48L3NhbWw6QXV0aG5Db250ZXh0%0D"
            + "%0APjwvc2FtbDpBdXRoblN0YXRlbWVudD48c2FtbDpBdHRyaWJ1dGVTdGF0ZW1lbnQ%2BPHNhbWw6QXR0%0D%0AcmlidXRlIE5hbWU9IkVtYWlsQWRkcm"
            + "VzcyI%2BPHNhbWw6QXR0cmlidXRlVmFsdWUgeG1sbnM6eHM9%0D%0AImh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hIiB4bWxuczp4c2k9Imh"
            + "0dHA6Ly93d3cu%0D%0AdzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIiB4c2k6dHlwZT0ieHM6c3RyaW5nIj5nYXJw%0D%0AaW5jQGdtYWlsLmNvb"
            + "Twvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjxzYW1s%0D%0AOkF0dHJpYnV0ZSBOYW1lPSJVc2VySUQiPjxzYW1sOkF0dHJpYnV0"
            + "ZVZhbHVlIHhtbG5zOnhzPSJo%0D%0AdHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSIgeG1sbnM6eHNpPSJodHRwOi8vd3d3Lncz%0D%0ALm9yZy"
            + "8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeHNpOnR5cGU9InhzOnN0cmluZyI%2BYm95Y2Vn%0D%0AYXJyeTwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3Nh"
            + "bWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0%0D%0AZSBOYW1lPSJGaXJzdE5hbWUiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhtbG5zOnhzPSJodHRwOi8v"
            + "%0D%0Ad3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8y%0D%0AMDAxL1hNTFNjaGVtYS1pbnN0YW5j"
            + "ZSIgeHNpOnR5cGU9InhzOnN0cmluZyI%2BR2Fycnk8L3NhbWw6%0D%0AQXR0cmlidXRlVmFsdWU%2BPC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ"
            + "1dGUgTmFtZT0iTGFz%0D%0AdE5hbWUiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhtbG5zOnhzPSJodHRwOi8vd3d3LnczLm9yZy8y%0D%0AMDAxL1hNTFNj"
            + "aGVtYSIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVt%0D%0AYS1pbnN0YW5jZSIgeHNpOnR5cGU9InhzOnN0cmluZyI%2BQm"
            + "95Y2U8L3NhbWw6QXR0cmlidXRlVmFs%0D%0AdWU%2BPC9zYW1sOkF0dHJpYnV0ZT48L3NhbWw6QXR0cmlidXRlU3RhdGVtZW50Pjwvc2FtbDpBc3Nl"
            + "%0D%0AcnRpb24%2BPC9zYW1scDpSZXNwb25zZT4%3D";

    
    @Test
    public void testDecodeBody() throws Exception {
        final MockWebContext webContext = MockWebContext.create();

        webContext.setRequestMethod("POST");
        final Pac4jHTTPPostDecoder decoder =
            new Pac4jHTTPPostDecoder(webContext);
        String message = "SAMLResponse=" + SAML_RESPONSE 
                + "&RelayState=https%3A%2F%2Flocalhost%3A8443%2Fanzo_authenticate%3Fclient_name%3DGSAML";

        webContext.setRequestContent(message);

        decode(decoder);
    }
    
    @Test
    public void testDecodeBodyNotAsQueryString() throws Exception {
        final MockWebContext webContext = MockWebContext.create();

        webContext.setRequestMethod("POST");
        final Pac4jHTTPPostDecoder decoder =
            new Pac4jHTTPPostDecoder(webContext);
        String message = URLDecoder.decode(SAML_RESPONSE, StandardCharsets.UTF_8.name());
        
        webContext.setRequestContent(message);

        decode(decoder);
    }
    
    @Test
    public void testDecodeParam() throws Exception {
        final MockWebContext webContext = MockWebContext.create();

        webContext.setRequestMethod("POST");
        final Pac4jHTTPPostDecoder decoder =
            new Pac4jHTTPPostDecoder(webContext);
        String message = URLDecoder.decode(SAML_RESPONSE, StandardCharsets.UTF_8.name());
        webContext.addRequestParameter("SAMLResponse", message);
        decode(decoder);
    }

    private void decode(final Pac4jHTTPPostDecoder decoder)
            throws ComponentInitializationException, MessageDecodingException {
        
        decoder.setParserPool(Configuration.getParserPool());
        decoder.initialize();
        decoder.decode();

        assertTrue(decoder.getMessageContext().getMessage() instanceof ResponseImpl);
    }



}
