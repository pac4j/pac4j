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
