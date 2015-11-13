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

insert into PAC4J_CFG(CLIENT_NAME, ENVIRONMENT, KEYSTORE_DATA, KEYSTORE_PASSWORD, PRIVATE_KEY_PASSWORD, IDP_METADATA, IDP_ENTITY_ID, SP_ENTITY_ID, MAX_AUTH_LIFETIME, DEST_BINDING_TYPE)
values ('One', 'UnitTest', X'0101010101', 'KsPwd1', 'PrKeyPwd1', 'CLOB-1', 'urn:idp1', 'urn:sp1', 1000, NULL);

insert into PAC4J_CFG(CLIENT_NAME, ENVIRONMENT, KEYSTORE_DATA, KEYSTORE_PASSWORD, PRIVATE_KEY_PASSWORD, IDP_METADATA, IDP_ENTITY_ID, SP_ENTITY_ID, MAX_AUTH_LIFETIME, DEST_BINDING_TYPE)
values ('Two', 'UnitTest', X'0202020202', 'KsPwd2', 'PrKeyPwd2', 'CLOB-2', 'urn:idp2', 'urn:sp2', 2000, 'http://redirect');

insert into PAC4J_CFG(CLIENT_NAME, ENVIRONMENT, KEYSTORE_DATA, KEYSTORE_PASSWORD, PRIVATE_KEY_PASSWORD, IDP_METADATA, IDP_ENTITY_ID, SP_ENTITY_ID, MAX_AUTH_LIFETIME, DEST_BINDING_TYPE)
values ('Three', 'UnitTest', X'0303030303', 'KsPwd3', 'PrKeyPwd3', 'CLOB-3', 'urn:idp3', 'urn:sp3', 3000, 'http://post');

insert into PAC4J_CFG(CLIENT_NAME, ENVIRONMENT, KEYSTORE_DATA, KEYSTORE_PASSWORD, PRIVATE_KEY_PASSWORD, IDP_METADATA, IDP_ENTITY_ID, SP_ENTITY_ID, MAX_AUTH_LIFETIME, DEST_BINDING_TYPE)
values ('Four', 'UnitTest', X'0404040404', 'KsPwd4', 'PrKeyPwd4', 'CLOB-4', 'urn:idp4', 'urn:sp4', 4000, NULL);

insert into PAC4J_CFG(CLIENT_NAME, ENVIRONMENT, KEYSTORE_DATA, KEYSTORE_PASSWORD, PRIVATE_KEY_PASSWORD, IDP_METADATA, IDP_ENTITY_ID, SP_ENTITY_ID, MAX_AUTH_LIFETIME, DEST_BINDING_TYPE)
values ('Five', 'UnitTest', X'0505050505', 'KsPwd5', 'PrKeyPwd5', 'CLOB-5', 'urn:idp5', 'urn:sp5', 5000, 'urn:binding');

insert into PAC4J_CFG(CLIENT_NAME, ENVIRONMENT, KEYSTORE_DATA, KEYSTORE_PASSWORD, PRIVATE_KEY_PASSWORD, IDP_METADATA, IDP_ENTITY_ID, SP_ENTITY_ID, MAX_AUTH_LIFETIME, DEST_BINDING_TYPE)
values ('Xxxx', 'DifferentEnv', X'FFFFFF', 'Passwd', 'Passwd', 'CLOB.....................................', 'urn:idpX', 'urn:spX', 111, NULL);
