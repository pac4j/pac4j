DECLARE
  Buffer RAW(10000); 
BEGIN
  -- The parameter to HEXTORAW is the binary content of the JKS store expressed as bytes in hexadecimal notation, e.g. '5F', 'AA', '9B' etc.
  Buffer := HEXTORAW('0102030405060708090A0B0C0D0E0F');
  UPDATE PAC4J_CFG
     SET Keystore_Data = Buffer
   WHERE Environment = 'MyEnvironment' AND CLIENT_NAME = 'SamlClient1';
END;
