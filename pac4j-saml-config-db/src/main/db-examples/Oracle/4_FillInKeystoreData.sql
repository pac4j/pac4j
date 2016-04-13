--
--   Copyright 2012 - 2015 pac4j organization
--
--   Licensed under the Apache License, Version 2.0 (the "License");
--   you may not use this file except in compliance with the License.
--   You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--   Unless required by applicable law or agreed to in writing, software
--   distributed under the License is distributed on an "AS IS" BASIS,
--   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--   See the License for the specific language governing permissions and
--   limitations under the License.
--

DECLARE
  Buffer RAW(10000); 
BEGIN
  -- The parameter to HEXTORAW is the binary content of the JKS store expressed as bytes in hexadecimal notation, e.g. '5F', 'AA', '9B' etc.
  Buffer := HEXTORAW('0102030405060708090A0B0C0D0E0F');
  UPDATE PAC4J_CFG
     SET Keystore_Data = Buffer
   WHERE Environment = 'MyEnvironment' AND CLIENT_NAME = 'SamlClient1';
END;
