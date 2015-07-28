Using pac4j against Microsoft ADFS 2.0
--------------------------------------

Follow these rules to successfully authenticate using Microsoft ADFS 2.0.


1. Entity ID
------------
Always specify an explicit Entity ID that does not contain any question mark. By default, pac4j uses the same Entity ID as the
AssertionConsumerService location, which contains the client's name as a parameter after a question mark. Unfortunately ADFS does not work
well with such IDs and starts an infinite redirection loop when A SAML message with such a message arrives.

This property is supported since pac4j 1.6.0.

Don't forget to change your metadata accordingly!


2. Maximum authentication time
------------------------------

pac4j has the default maximum time set to 1 hour while ADFS has it set to 8 hours. Therefore it can happen that ADFS sends
an assertion which is still valid on ADFS side but evaluated as invalid on pac4j side.

You can see the following error message:
org.pac4j.saml.exceptions.SAMLException: Authentication issue instant is too old or in the future

There are two possibilities how to make the values equal:
- Change the value in ADFS management console in the trust properties dialog.
- Change the value on pac4j side.


3. Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
---------------------------------------------------------------------------------

You must install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files into your JRE/JDK
running pac4j. If you don't do it, you may encounter errors like this:

ERROR [org.opensaml.xml.encryption.Decrypter] - <Error decrypting the encrypted data element>
org.apache.xml.security.encryption.XMLEncryptionException: Illegal key size
ERROR [org.opensaml.xml.encryption.Decrypter] - <Failed to decrypt EncryptedData using either EncryptedData KeyInfoCredentialResolver or EncryptedKeyResolver + EncryptedKey KeyInfoCredentialResolver>
ERROR [org.opensaml.saml2.encryption.Decrypter] - <SAML Decrypter encountered an error decrypting element content>

Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files can be downloaded from Oracle's Java Download site.
