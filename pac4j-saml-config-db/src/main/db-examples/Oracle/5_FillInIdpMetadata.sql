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
  BufferVC VARCHAR2(32000);
  ResultClob CLOB;
BEGIN
	DBMS_LOB.CreateTemporary(ResultClob, TRUE);

	-- If the metadata text is longer than 32.000 B, you must cut it into pieces and call DBMS_LOB.Append repeatedly.
	BufferVC := '<EntityDescriptor ID="abcdefgh" entityID="urn:idp1" xmlns="urn:oasis:names:tc:SAML:2.0:metadata">
... very long XML here ...
</EntityDescriptor>
';
	DBMS_LOB.Append(ResultClob, BufferVC);

  UPDATE PAC4J_CFG
     SET IdP_Metadata = ResultClob
   WHERE Client_Name = 'SamlClient1';

  DBMS_LOB.FreeTemporary(ResultClob);
END;
