-- SAML client with the default (POST) binding
insert into PAC4J_CFG(SSPCC_ID,Client_Name,Environment,Keystore_Data,Keystore_Password,Private_Key_Password,IdP_Metadata,IdP_Entity_ID,SP_Entity_ID,Max_Auth_Lifetime,Dest_Binding_Type)
values
(
	PAC4J_CFG_SEQ.nextval,
	'SamlClient1',
	'MyEnvironment',
	EMPTY_BLOB(),
	'KsPwd1',
	'PrKeyPwd1',
	EMPTY_CLOB(),
	'urn:idp1',
	'urn:sp1',
	3600,
	NULL
);

-- SAML client with an explicit binding (Redirect)
insert into PAC4J_CFG(SSPCC_ID,Client_Name,Environment,Keystore_Data,Keystore_Password,Private_Key_Password,IdP_Metadata,IdP_Entity_ID,SP_Entity_ID,Max_Auth_Lifetime,Dest_Binding_Type)
values
(
	PAC4J_CFG_SEQ.nextval,
	'SamlClient2',
	'MyEnvironment',
	EMPTY_BLOB(),
	'KsPwd2',
	'PrKeyPwd2',
	EMPTY_CLOB(),
	'urn:idp2',
	'urn:sp2',
	3600,
	'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect'
);
